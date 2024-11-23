import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { User } from '../../../user/models/user.model';
import { Participant } from '../../../participant/models/participant.model';
import { transformGenderType, transformParticipantStatus, transformParticipantType, transformRoleType } from '../../utils/common-utils';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnChanges {

  @Input({ required: true }) content!: User | Participant;

  name!: string;
  type!: string;
  extras: string[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['content'] && this.content) {
      this.name = String(this.content.name);
      this.setProfiledetails();
    }
  }
  
  private setProfiledetails(): void {
    if (this.isParticipant(this.content)) {
      const gender = transformGenderType(this.content.gender).toLocaleLowerCase();
      const status = transformParticipantStatus(this.content.isActive).toLocaleLowerCase();

      this.type = transformParticipantType(this.content.type);
      this.extras = [];
      this.extras.push(`Gênero: ${gender}`, `Status: ${status}`);
      return;
    } 
    if (this.isUser(this.content)) {
      this.type = transformRoleType(this.content.role);
      this.extras = [`Email: ${this.content.email}`];
      return;
    }
  }

  private isParticipant(content: User | Participant): content is Participant {
    return (content as Participant).type !== undefined;
  }
  
  private isUser(content: User | Participant): content is User {
    return (content as User).role !== undefined;
  }

}
