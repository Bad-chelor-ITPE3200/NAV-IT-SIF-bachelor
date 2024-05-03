package com.bachelor.vju_vm_apla2.Models.POJO.Dokarkiv;

import com.bachelor.vju_vm_apla2.Models.POJO.Saf.AvsenderMottaker;
import com.bachelor.vju_vm_apla2.Models.POJO.Saf.Sak;
import com.bachelor.vju_vm_apla2.Models.POJO.Saf.Tilleggsopplysning;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class OppdaterJournalpost {
    private AvsenderMottaker avsenderMottaker;
    private Bruker bruker;
    private String tema;
    private String behandlingstema;
    private String tittel;
    @Size(min = 4, max = 4, message = "journalfoerendEnhet must be exactly 4 characters")
    private String journalfoerendEnhet;
    private Date datoRetur;
    private Date datoMottatt;
    private Date datoDokument;
    private Tilleggsopplysning tilleggsopplysninger;
    private Sak sak;
    private Dokumenter dokumenter;
}
