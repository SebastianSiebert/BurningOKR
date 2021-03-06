import { Injectable } from '@angular/core';
import { UserApiService } from '../api/user-api.service';
import { Observable } from 'rxjs/internal/Observable';
import { User } from '../../model/api/user';
import { map } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserMapper {
// TODO: After the removal of the view-user-type, transform user mapper service to a user service
  constructor(private userApiService: UserApiService) {
  }
  users$: BehaviorSubject<User[]>;

  getUserById$(objectId: string): Observable<User> {
    if (this.users$) {
      return this.users$.pipe(
        map((users: User[]) => {
          const index: number = users.findIndex(u => u.id === objectId);

          return index !== -1 ? users[index] : undefined;
        })
      );
    } else {
      return this.userApiService.getUserById$(objectId)
        .pipe(
          map((user: User) => {
            return user;
          })
        );
    }
  }

  getAllUsers$(): Observable<User[]> {
    if (this.users$) {
      return this.users$.asObservable();
    } else {
      this.users$ = new BehaviorSubject<User[]>([]);
      this.userApiService
        .getUsers$()
        .pipe(
          map((users: User[]) => {
            return users;
          }))
        .subscribe(u => this.users$.next(u));

      return this.users$;
    }
  }

  getUsers$(): Observable<User[]> {
    if (this.users$) {
      return this.users$.asObservable();
    } else {
      return this.getAllUsers$();
    }
  }

  addAdmin$(adminToAdd: User): Observable<User> {
    return this.userApiService.addAdmin$(
      new User(
        adminToAdd.id,
        adminToAdd.givenName,
        adminToAdd.surname,
        adminToAdd.email,
        adminToAdd.jobTitle,
        adminToAdd.department,
        adminToAdd.photo
      )
    );
  }

  deleteAdmin$(adminToDeleteId: string): Observable<boolean> {
    return this.userApiService.deleteAdmin$(adminToDeleteId);
  }
}
