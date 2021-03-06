import { Component, OnDestroy, OnInit } from '@angular/core';
import { ControlHelperService } from '../../../shared/services/helper/control-helper.service';
import { MatDialogRef } from '@angular/material';
import { DialogComponent } from '../../../shared/components/dialog-component/dialog.component';
import { User } from '../../../shared/model/api/user';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CurrentUserService } from '../../services/current-user.service';
import { NEVER, Observable } from 'rxjs';
import { FeedbackMapper } from '../feedback.mapper';
import { map, take } from 'rxjs/operators';

@Component({
  selector: 'app-feedback-form',
  templateUrl: './feedback-form.component.html',
  styleUrls: ['./feedback-form.component.scss']
})
export class FeedbackFormComponent implements OnInit, OnDestroy {
  feedbackForm$: Observable<FormGroup>;
  getErrorMessage = this.controlHelperService.getErrorMessage;
  currentUser$: Observable<User>;

  constructor(private dialogRef: MatDialogRef<DialogComponent<object>>,
              private feedbackMapper: FeedbackMapper,
              private controlHelperService: ControlHelperService,
              private currentUserService: CurrentUserService) {
  }

  ngOnInit(): void {
    this.currentUser$ = this.currentUserService.getCurrentUser();
    this.feedbackForm$ = this.getFeedbackForm$();
  }

  ngOnDestroy(): void {
    this.closeDialog();
  }

  sendFeedback(formRawValue: { feedbackText: string; name: string; }): void {
    const feedbackText: string = formRawValue.feedbackText;
    const name: string = formRawValue.name;
    this.dialogRef.afterClosed()
      .subscribe(_ => {
      this.feedbackMapper.postFeedback$(feedbackText, name)
        .pipe(take(1))
        .subscribe();
    });
    this.closeDialog();
  }

  closeDialog(): void {
    this.dialogRef.close(NEVER);
  }

  private getFeedbackForm$(): Observable<FormGroup> {
    return this.currentUser$.pipe(
      map((currentUser: User) => {
        return new FormGroup({
          feedbackText: new FormControl('', [Validators.required]),
          name: new FormControl(`${currentUser.givenName} ${currentUser.surname}`,
            [Validators.required])
        });
      })
    );
  }
}
