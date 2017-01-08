module ums{
  export interface AdmissionStudent{
    id:number;
    semesterId:number;
    semesterName:string;
    programType:number;
    receiptId:string;
    pin:string;
    hscBoard:string;
    hscRoll:string;
    hscRegNo:string;
    hscYear:number;
    hscGroup:string;
    sscBoard:string;
    sscRoll:string;
    sscYear:number;
    sscGroup:string;
    gender:string;
    dateOfBirth:string;
    studentName:string;
    fatherName:string;
    motherName:string;
    sscGpa:number;
    hscGpa:number;
    quota:string;
    unit:string;
    admissionRoll:string;
    meritSlNo:number;
    studentId:string;
    allocatedProgramId:number;
    programShortName:string;
    programLongName:string;
    presentStatus:number;
    text:string;
    programId:number;
    programName:string;
    allocatedSeat:number;
    selected:number;
    remaining:number;
    waiting:number;
  }
}