import { ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { PageBodyComponent } from '../../../core/components/page-body/page-body.component';
import { RegistrationReport } from '../../models/registration-report.model';
import { InactivationReport } from '../../models/inactivation-report.model';
import { NgClass } from '@angular/common';
import { SelectButtonComponent } from "../../../shared/components/select-button/select-button.component";
import { Option } from '../../../shared/models/option.model';
import { ParticipantsTableComponent } from "../../ui/participants-table/participants-table.component";
import { BehaviorSubject, tap } from 'rxjs';
import { ParticipantService } from '../../services/participant.service';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';
import { HttpResponse } from '@angular/common/http';
import { AlertPopupComponent } from "../../../shared/components/alert-popup/alert-popup.component";

@Component({
  selector: 'app-participants-files-manager',
  standalone: true,
  imports: [
    NgClass,
    PageBodyComponent,
    SelectButtonComponent,
    ParticipantsTableComponent,
    AlertPopupComponent
],
  templateUrl: './participants-files-manager.component.html',
  styleUrl: './participants-files-manager.component.scss'
})
export class ParticipantsFilesManagerComponent implements OnInit {

  private router = inject(Router);
  private changeDetector = inject(ChangeDetectorRef);
  private participantService = inject(ParticipantService);

  @ViewChild('removeReportPopup') removeReportPopup!: AlertPopupComponent;
  @ViewChild('uploadFilePopup') uploadFilePopup!: AlertPopupComponent;

  registrationReport$ = new BehaviorSubject<RegistrationReport>({
    total: 0, added: 0, notAdded: 0, registered: 0, problems: 0, rejected: 0, notRegistered: 0, participantsWithProblems: []
  });

  inactivationReport$ = new BehaviorSubject<InactivationReport>({
    total: 0, inactivated: 0, notInactivated: 0, problems: 0, participantsWithProblems: []
  });

  mode: 'registration' | 'inactivation' = 'registration';
  reportTitle: string = 'Relatório de Inscrição';

  currentRegistrationFile!: File;
  currentInactivationFile!: File;
  currentFileName!: string;
  fileName!: string;
  registrationErrorMessage!: string;
  inactivationErrorMessage!: string;

  isReportEmpty: boolean = true;
  canUpload: boolean = false;
  canDownload: boolean = false;
  isRequesting: boolean = false;

  modeOptions: Option[] = [
    { name: 'Inscrever por CSV', value: 'registration' },
    { name: 'Desativar por CSV', value: 'inactivation' }
  ];

  ngOnInit() {
    const registrationReportString = localStorage.getItem('registrationReport');
    const inactivationReportString = localStorage.getItem('inactivationReport');

    if (registrationReportString) {
      this.registrationReport$.next(JSON.parse(registrationReportString));
      this.canDownload = this.registrationReport$.value.participantsWithProblems.length > 0;
      this.isReportEmpty = this.checkReportFields(this.registrationReport$.value);
      return;
    }
    if (inactivationReportString) {
      this.inactivationReport$.next(JSON.parse(inactivationReportString));
      this.canDownload = this.inactivationReport$.value.participantsWithProblems.length > 0;
      this.isReportEmpty = this.checkReportFields(this.registrationReport$.value);
      return;
    }
    this.changeDetector.detectChanges();
  }

  comeBack(): void {
    document.documentElement.scrollTop = 0;
    this.router.navigate(['/participants']);
  }

  onSwitchMode(value: string | number) {
    switch (value) {
      case 'registration':
        this.mode = 'registration';
        this.isReportEmpty = this.checkReportFields(this.registrationReport$.value);
        this.currentFileName = this.currentRegistrationFile?.name || '';
        this.canUpload = this.currentRegistrationFile !== undefined;
        this.canDownload = this.registrationReport$.value.participantsWithProblems.length !== 0;
        break;
      case 'inactivation':
        this.mode = 'inactivation';
        this.isReportEmpty = this.checkReportFields(this.inactivationReport$.value);
        this.currentFileName = this.currentInactivationFile?.name || '';
        this.canUpload = this.currentInactivationFile !== undefined;
        this.canDownload = this.inactivationReport$.value.participantsWithProblems.length !== 0;
        break;
    }
  }

  showPopup(event: Event, id: 'removeReportPopup' | 'uploadFilePopup'): void {
    event.stopPropagation();

    switch (id) {
      case 'removeReportPopup':
        this.removeReportPopup.openModal();
        break;
      case 'uploadFilePopup':
        if (this.mode === 'registration' && this.registrationReport$.value.participantsWithProblems.length !== 0) {
          this.uploadFilePopup.openModal();
          return;
        }
        if (this.mode === 'inactivation' && this.inactivationReport$.value.participantsWithProblems.length !== 0) {
          this.uploadFilePopup.openModal();
          return;
        }
        this.submitFile();
        break;
    }
  }

  handleRemoveReport(): void {
    switch (this.mode) {
      case 'registration':
        this.registrationReport$.next({
          total: 0, added: 0, notAdded: 0, registered: 0, problems: 0, rejected: 0, notRegistered: 0, participantsWithProblems: []
        });
        localStorage.removeItem('registrationReport');
        break;
      case 'inactivation':
        this.inactivationReport$.next({ 
          total: 0, inactivated: 0, notInactivated: 0, problems: 0, participantsWithProblems: [] 
        });
        localStorage.removeItem('inactivationReport');
        break;
    }
    this.canDownload = false;
    this.isReportEmpty = true;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    const extention = file.name.split('.').reverse()[0];

    if (extention !== 'csv') {
      this.canUpload = false;
      this.currentFileName = 'Arquivo inválido';
      return;
    }
    switch (this.mode) {
      case 'registration':
        this.currentRegistrationFile = file;
        this.currentFileName = this.currentRegistrationFile.name;
        this.canUpload = this.currentRegistrationFile !== undefined;
        break;
      case 'inactivation':
        this.currentInactivationFile = file;
        this.currentFileName = this.currentInactivationFile.name;
        this.canUpload = this.currentInactivationFile !== undefined;
        break;
    }
    input.value = '';
  }

  submitFile(): void {
    switch(this.mode) {
      case 'registration':
        this.submitRegirstrationFile(); break;
      case 'inactivation':
        this.submitInactivationFile(); break;
    }
  }

  private submitRegirstrationFile(): void {
    if (!this.currentRegistrationFile) return;

    this.isRequesting = true;
    this.participantService.uploadRegistrarionCSVFile(this.currentRegistrationFile).subscribe({
      next: report => {
        this.setReport('registrationReport', report);
        this.registrationReport$.next(report);
        this.canDownload = report.participantsWithProblems.length > 0;
        this.isReportEmpty = this.checkReportFields(report);
        this.registrationErrorMessage = '';
      },
      error: (err: ExceptionResponse) => {
        this.registrationErrorMessage = err.details;
      }
    });
    this.isRequesting = false;
  }

  private submitInactivationFile(): void {
    if (!this.currentInactivationFile) return;

    this.isRequesting = true;
    this.participantService.uploadInactivationCSVFile(this.currentInactivationFile).subscribe({
      next: report => {
        this.setReport('inactivationReport', report);
        this.inactivationReport$.next(report);
        this.canDownload = report.participantsWithProblems.length > 0;
        this.isReportEmpty = this.checkReportFields(report);
        this.inactivationErrorMessage = '';
      },
      error: (err: ExceptionResponse) => {
        this.inactivationErrorMessage = err.details;
      }
    });
    this.isRequesting = false;
  }

  private setReport(name: string, report: RegistrationReport | InactivationReport): void {
    const existingReport = localStorage.getItem(name);

    if (existingReport) {
      localStorage.removeItem(name);
    }
    localStorage.setItem(name, JSON.stringify(report));
  }

  private checkReportFields(report: RegistrationReport | InactivationReport): boolean {
    return Object.entries(report)
      .filter(([_, value]) => typeof value === 'number')
      .every(([_, value]) => value === 0);

  }

  onDownloadFile(): void {
    const content = this.registrationReport$.value.participantsWithProblems;

    if (content.length === 0) return;

     this.participantService.downloadReportCSVFile(this.mode, content).subscribe({
      next: (response: HttpResponse<Blob>) => {
        const blob = response.body!;
        const contentDisposition = response.headers.get('Content-Disposition');
        const filename = contentDisposition?.split('filename=')[1]?.trim() || 'relatorio-de-registros.csv';

        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');

        link.href = url;
        link.download = filename.replace(/['"]/g, '');
        link.click();
        
        window.URL.revokeObjectURL(url);
      },
      error: (err: ExceptionResponse) => {
        this.registrationErrorMessage = err.details;
      }
     });
  }

}
