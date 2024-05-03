package com.bachelor.vju_vm_apla2.Models.DTO.DokArkiv;

import com.bachelor.vju_vm_apla2.Models.POJO.Dokarkiv.*;
import lombok.Data;

@Data
public class CreateJournalpost_DTO {
    public String journalpostID;
    public CreateJournalpost oldMetadata;
    public CreateJournalpost newMetadata;
}
