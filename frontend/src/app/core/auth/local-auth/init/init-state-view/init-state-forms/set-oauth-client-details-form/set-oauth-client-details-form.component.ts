import { Component, EventEmitter, OnInit } from '@angular/core';
import { InitStateFormComponent } from '../init-state-form/init-state-form.component';
import { FormBuilder, Validators } from '@angular/forms';
import { InitState } from '../../../init-state';
import { InitService } from '../../../init.service';
import { OauthClientDetails } from '../../../../../../../shared/model/api/oauth-client-details';
import { FormGroupTyped } from '../../../../../../../../typings';
import { Consts } from '../../../../../../../shared/consts';
import { catchError, switchMap } from 'rxjs/operators';
import { Observable, of, throwError } from 'rxjs';
import { OAuthFrontendDetailsService } from '../../../../../services/o-auth-frontend-details.service';

@Component({
  selector: 'app-set-oauth-client-details-form',
  templateUrl: './set-oauth-client-details-form.component.html',
  styleUrls: ['./set-oauth-client-details-form.component.css']
})
export class SetOauthClientDetailsFormComponent extends InitStateFormComponent implements OnInit {
  form: FormGroupTyped<OauthClientDetails>;
  eventEmitter: EventEmitter<InitState> = new EventEmitter<InitState>();
  oauthClientDetails: OauthClientDetails = new OauthClientDetails();
  spinnerIsHidden: boolean = true;

  constructor(
    private formBuilder: FormBuilder,
    private initService: InitService,
    private oAuthFrontendDetails: OAuthFrontendDetailsService
  ) {
    super();
  }

  ngOnInit(): void {
    this.generateOauthClientDetailsForm();
  }

  private generateOauthClientDetailsForm(): void {
    this.form = this.formBuilder.group({
      accessTokenValidity: [this.oauthClientDetails.accessTokenValidity, [Validators.required, Validators.min(Consts.MIN_TOKEN_DURATION)]],
      clientId: [this.oauthClientDetails.clientId, [Validators.required]],
      clientSecret: [this.oauthClientDetails.clientSecret, [Validators.required]],
      refreshTokenValidity: [this.oauthClientDetails.refreshTokenValidity,
                             [Validators.required, Validators.min(Consts.MIN_TOKEN_DURATION)]],
      webServerRedirectUri: [this.oauthClientDetails.webServerRedirectUri, [Validators.required]]
    }) as FormGroupTyped<OauthClientDetails>;
    this.form.disable();
  }

  private handleSubmitClick(): void {
    this.toggleLoadingScreen();
    this.changeOauthClientDetailsBasedOnFormData();
    this.initService.postOauthClientDetails$(this.oauthClientDetails)
      .pipe(
        switchMap(initState => this.checkIfInitStateHasChanged(initState))
      )
      .subscribe(initState => {
        this.oAuthFrontendDetails.reloadOAuthFrontendDetails();
        this.eventEmitter.emit(initState);
        this.toggleLoadingScreen();
      }, () => {
        this.toggleLoadingScreen();
      });
  }

  disOrEnableForm(): void {
    if (this.form.enabled) {
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  private changeOauthClientDetailsBasedOnFormData(): void {
    const typedForm: FormGroupTyped<OauthClientDetails> = this.form;
    this.oauthClientDetails = typedForm.getRawValue();
  }

  private checkIfInitStateHasChanged(initState: InitState): Observable<InitState> {
    if (!!initState) {
      return this.initService.getInitStateAndBypassErrorHandling$()
        .pipe(switchMap(newInitState => {
            if (newInitState.runtimeId !== initState.runtimeId) {
              return of(newInitState);
            } else {
              return this.checkIfInitStateHasChanged(initState);
            }
          }),
          catchError(() => this.checkIfInitStateHasChanged(initState))
        );
    } else {
      return throwError('no init state given');
    }
  }

  private toggleLoadingScreen(): void {
    this.spinnerIsHidden = !this.spinnerIsHidden;
  }
}
