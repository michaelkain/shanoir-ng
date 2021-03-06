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

import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Account} from './account';

@Injectable()
export class AccountEventsService extends Subject<any> {
    constructor() {
        super();
    }
    
    loginSuccess(account:any) {
        if(account) {
            account.authenticated = true;
            super.next(account);
        }
    }
    
    logout(account:any) {
        if(account) {
            account.authenticated = false;
            super.next(account);
        }
    }

}
