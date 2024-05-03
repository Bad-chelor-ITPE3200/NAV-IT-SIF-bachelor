package com.bachelor.vju_vm_apla2.Models.DTO.DokArkiv;

import lombok.Data;

import java.util.Date;

// Kan bruke OppdaterJournalpost klassen under Dokarkiv mappen for fremtidig bruk, men i vår tilfelle er det lettere å bruke kun denne.
@Data
public class OppdaterJournalpost_DTO {
    public String journalpostID;
    public Date mottattDato;
}
