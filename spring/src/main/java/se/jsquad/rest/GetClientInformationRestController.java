/*
 * Copyright 2020 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.rest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jsquad.api.client.info.AccountTransactionRequest;
import se.jsquad.ejb.AccountTransactionEjbLocal;
import se.jsquad.ejb.ClientInformationEjbLocal;

@RestController
@RequestMapping(path = "/api")
public class GetClientInformationRestController {
    private Logger logger;
    private ClientInformationEjbLocal clientInformationEjbLocal;
    private AccountTransactionEjbLocal accountTransactionEjbLocal;

    @Autowired
    private GetClientInformationRestController(Logger logger, ClientInformationEjbLocal clientInformationEjbLocal,
                                               AccountTransactionEjbLocal accountTransactionEjbLocal) {
        this.logger = logger;
        this.clientInformationEjbLocal = clientInformationEjbLocal;
        this.accountTransactionEjbLocal = accountTransactionEjbLocal;
    }

    @GetMapping(value = "/client/info/{personIdentification}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getClientInformation(@PathVariable String personIdentification) {
        try {
            return ResponseEntity.ok(clientInformationEjbLocal.getClient(personIdentification));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body("Internal server error.");
        }
    }

    @PostMapping(value = "/account/transfer", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> postAccountTransfer(@RequestBody AccountTransactionRequest accountTransactionRequest) {
        this.accountTransactionEjbLocal.transferValueFromAccountToAccount(Integer.valueOf(accountTransactionRequest
                .getCreateAccountTransaction().getValue()), accountTransactionRequest
                .getCreateAccountTransaction().getFromAccountNumber(), accountTransactionRequest
                .getCreateAccountTransaction().getToAccountNumber());

        return ResponseEntity.ok().build();
    }
}
