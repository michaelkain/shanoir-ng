/**
 * Shanoir NG - Import, manage and share neuroimaging data
 * Copyright (C) 2009-2019 Inria - https://www.inria.fr/
 * Contact us on https://project.inria.fr/shanoir/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/gpl-3.0.html
 */
import { Component } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AcquisitionEquipment } from '../../acquisition-equipments/shared/acquisition-equipment.model';
import { IdName } from '../../shared/models/id-name.model';

import { AcquisitionEquipmentService } from '../../acquisition-equipments/shared/acquisition-equipment.service';
import { CenterService } from '../../centers/shared/center.service';
import { NiftiConverterService } from '../../niftiConverters/nifti.converter.service';
import { EntityComponent } from '../../shared/components/entity/entity.component.abstract';
import { StudyService } from '../../studies/shared/study.service';
import { StudyCard } from '../shared/study-card.model';
import { StudyCardService } from '../shared/study-card.service';

@Component({
    selector: 'study-card',
    templateUrl: 'study-card.component.html',
    styleUrls: ['study-card.component.css']
})
export class StudyCardComponent extends EntityComponent<StudyCard> {

    private centers: IdName[] = [];
    private studies: IdName[] = [];
    private acquisitionEquipments: Map<string, AcquisitionEquipment[]> = new Map();
    private niftiConverters: IdName[] = [];

    constructor(
            private route: ActivatedRoute,
            private studyCardService: StudyCardService, 
            private centerService: CenterService,
            private studyService: StudyService,
            private acqEqService: AcquisitionEquipmentService,
            private niftiConverterService: NiftiConverterService) {
        super(route, 'study-card');
    }

    get studyCard(): StudyCard { return this.entity; }
    set studyCard(coil: StudyCard) { this.entity = coil; }

    initView(): Promise<void> {
        return this.studyCardService.get(this.id).then(sc => {
            this.studyCard = sc;
        });
    }

    initEdit(): Promise<void> {
        this.fetchStudies();
        this.fetchNiftiConverters();
        return this.studyCardService.get(this.id).then(sc => {
            this.studyCard = sc;
        });
    }

    initCreate(): Promise<void> {
        this.fetchStudies();
        this.fetchNiftiConverters();
        this.studyCard = new StudyCard();
        return Promise.resolve();
    }

    buildForm(): FormGroup {
        let form: FormGroup = this.formBuilder.group({
            'name': [this.studyCard.name, [Validators.required, Validators.minLength(2)]],
            'study': [this.studyCard.study, [Validators.required]],
            'acquisitionEquipment': [this.studyCard.acquisitionEquipment, [Validators.required]],
            'niftiConverter': [this.studyCard.niftiConverter, [Validators.required]],
        });
        this.subscribtions.push(
            form.get('study').valueChanges.subscribe(study => this.onStudyChange(study, form))
        );

        return form;
    }

    public hasEditRight(): boolean {
        return this.keycloakService.isUserAdminOrExpert();
    }
    
    private fetchStudies() {
        this.studyService.getStudiesNames()
            .then(studies => this.studies = studies);
    }
    
    private fetchCenters(studyId: number): Promise<IdName[]> {
        return this.centerService.getCentersNamesByStudyId(studyId)
            .then(centers => this.centers = centers);
    }

    private fetchAcqEq(studyId: number): Promise<void> {
        return this.acqEqService.getAllByStudy(studyId)
            .then(acqEqs => {
                this.acquisitionEquipments = new Map();
                for (let acqEq of acqEqs) {
                    if (!this.acquisitionEquipments.has(acqEq.center.name)) {
                        this.acquisitionEquipments.set(acqEq.center.name, []);
                    }
                    this.acquisitionEquipments.get(acqEq.center.name).push(acqEq);
                }
            });
    }

    private fetchNiftiConverters() {
        this.niftiConverterService.getAll()
            .then(converters => this.niftiConverters = converters);
    }

    private onStudyChange(study: IdName, form: FormGroup) {
        if (study) {
            this.fetchAcqEq(study.id).then(() => {
                if (this.studyCard.acquisitionEquipment) {
                    let found: boolean = false;
                    this.acquisitionEquipments.forEach((acqEqList: AcquisitionEquipment[], centerName: string) => {
                        for (let acqEq of acqEqList) {
                            if (acqEq.id == this.studyCard.acquisitionEquipment.id) {
                                found = true;
                                return;
                            }
                        }
                    });
                    if (!found) this.studyCard.acquisitionEquipment = null;
                }
            });
            form.get('acquisitionEquipment').enable();
        } else {
            form.get('acquisitionEquipment').disable();
            this.studyCard.acquisitionEquipment = null;
            this.acquisitionEquipments = new Map();
        }
    }

}