import { IdNameObject } from "./id-name-object.model";

//import { Center } from '../../centers/shared/center.model';

export class Examination {
    id: number;
    examinationDate: Date;
    examinationExecutive: IdNameObject;
    subject: IdNameObject;
    study: IdNameObject;
    center: IdNameObject;
    comment: String;
    note: String;
    subjectWeight: number;
}