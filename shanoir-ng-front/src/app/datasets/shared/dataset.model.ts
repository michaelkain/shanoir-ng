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

import { Entity } from '../../shared/components/entity/entity.abstract';
import { ServiceLocator } from '../../utils/locator.service';
import { DatasetProcessing } from './dataset-processing.model';
import { DatasetType } from './dataset-type.model';
import { DatasetService } from './dataset.service';
import { Study } from '../../studies/shared/study.model';
import { Subject } from '../../subjects/shared/subject.model';
import { MrDataset } from '../dataset/mr/dataset.mr.model';
import { getDatasetInstance } from '../../utils/app.utils'
import { DatasetDTO } from './dataset.dto';

declare type ExploredEntity = 'ANATOMICAL_DATASET' | 'FUNCTIONAL_DATASET' | 'HEMODYNAMIC_DATASET' | 'METABOLIC_DATASET' | 'CALIBRATION';
declare type ProcessedDatasetType = 'RECONSTRUCTEDDATASET' | 'NONRECONSTRUCTEDDATASET';
declare type CardinalityOfRelatedSubjects = 'SINGLE_SUBJECT_DATASET' | 'MULTIPLE_SUBJECTS_DATASET';

export abstract class Dataset extends Entity {
    
    id: number;
    creationDate: Date;
    name: string;
    type: DatasetType;
    //datasetAcquisition: DatasetAcquisition
    //datasetExpressions: List<DatasetExpression>
    //datasetProcessing: DatasetProcessing
    //groupOfSubjectsId: number;
    //inputOfDatasetProcessings: Array<InputOfDatasetProcessing>
    //referencedDatasetForSuperimposition: Dataset;
    study : Study;
    subject : Subject;
    originMetadata: DatasetMetadata;
    updatedMetadata : DatasetMetadata = new DatasetMetadata();

    service: DatasetService = ServiceLocator.injector.get(DatasetService);

    public stringify() {
        return JSON.stringify(new DatasetDTO(this), this.replacer);
    }

    static getDatasetInstance(type: DatasetType): Dataset {
        // switch(type) {
        //     case 'Mr': return new MrDataset();
        //     //case 'Pet': return new PetDataset();
        //     default: throw new Error(type + ' is not an implemented dataset type');
        // }
        return getDatasetInstance(type); // temporary
    }
}

export class DatasetMetadata {
    comment: string;
    datasetModalityType: string;
    exploredEntity: ExploredEntity;
    name: string;
    processedDatasetType: ProcessedDatasetType;
    cardinalityOfRelatedSubjects: CardinalityOfRelatedSubjects = 'SINGLE_SUBJECT_DATASET';
}