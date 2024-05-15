package com.bachelor.vju_vm_apla2;

import com.bachelor.vju_vm_apla2.Controller.SafController;
import com.bachelor.vju_vm_apla2.Models.DTO.Saf.ReturnFromGraphQl_DTO;
import com.bachelor.vju_vm_apla2.Models.DTO.Request.SokJournalpostList_DTO;
import com.bachelor.vju_vm_apla2.Models.POJO.Saf.*;
import com.bachelor.vju_vm_apla2.Service.Saf_Service.SafService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class JournalPostControllerTest {

    @Mock
    SafService serviceMock;
    @InjectMocks
    SafController safController;


    @Mock
    WebClient wc;
    @Test
    public void hentJournalpostTest() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer ");
        Journalpost jp1 = new Journalpost();
        List<Journalpost> JPtester = new ArrayList<>();
        JPtester.add(jp1);

        List <Journalposttype> jpts = new ArrayList<>();
        Journalposttype JT1 = Journalposttype.N;
        jpts.add(JT1);
        List <Journalstatus> jptts = new ArrayList<>();
        jptts.add(Journalstatus.MOTTATT);
        List<Tema>tt = new ArrayList<>();

        tt.add(Tema.AAP);
        BrukerIdInput bIdInput= new BrukerIdInput("001",BrukerIdType.FNR);
        SokJournalpostList_DTO brukerId = new SokJournalpostList_DTO(new BrukerIdInput(bIdInput.getId(), bIdInput.getType()), "2024-12-12", "2025-12-12", jpts, jptts, tt);
        Dokumentoversikt  dO = new Dokumentoversikt();
        ReturnFromGraphQl_DTO fgqlTest = new ReturnFromGraphQl_DTO(dO, "hello world");
        Mono <ReturnFromGraphQl_DTO> MfgglTest = Mono.just(fgqlTest);
        Mockito.when(serviceMock.hentJournalpostListe(any(SokJournalpostList_DTO.class), any(HttpHeaders.class))).thenReturn(MfgglTest); //headers and stuff dont get sendt, thats why error is getting there
        Mono<ResponseEntity<ReturnFromGraphQl_DTO>> resultMono = safController.hentJournalpostListe(brukerId, headers);
        //Not sure how this works, but rolls with it for now
        StepVerifier.create(resultMono).assertNext(fraGrapQlDtoResponseEntity -> {
            assertEquals(HttpStatus.OK, fraGrapQlDtoResponseEntity.getStatusCode());
            assertEquals("application/json", fraGrapQlDtoResponseEntity.getHeaders().getContentType().toString());
            assertEquals("inline", fraGrapQlDtoResponseEntity.getHeaders().getFirst("Content-Disposition"));
            assertEquals(fgqlTest, fraGrapQlDtoResponseEntity.getBody());
        });
    }

    @Test(expected=Exception.class)
    public void hentJornalpostThrowE(){
        Dokumentoversikt  dO = new Dokumentoversikt();
        ReturnFromGraphQl_DTO fgqlTest = new ReturnFromGraphQl_DTO(dO, "hello world");
        Journalpost jp1 = new Journalpost();
        List<Journalpost> JPtester = new ArrayList<>();
        JPtester.add(jp1);

        List <Journalposttype> jpts = new ArrayList<>();
        Journalposttype JT1 = Journalposttype.N;
        jpts.add(JT1);
        List <Journalstatus> jptts = new ArrayList<>();
        jptts.add(Journalstatus.MOTTATT);
        List<Tema>tt = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        BrukerIdInput bk0 = new BrukerIdInput("hei", BrukerIdType.FNR);
        SokJournalpostList_DTO DTO = new SokJournalpostList_DTO(bk0, "2023-10-20", "2023-11-20",jpts, jptts, tt);
        headers.add("Authorization", "bearer");
        //trying without mocking anything as we will throw an exception
        Mockito.when(serviceMock.hentJournalpostListe(any(SokJournalpostList_DTO.class), any(HttpHeaders.class))).thenThrow(new Exception("generic cool exception"));
        String res = String.valueOf(safController.hentJournalpostListe(DTO, headers));

        assertEquals(any(Exception.class), res);
    }
    @Test
    public void hentJournalpostTestFail() {

        HttpHeaders headers = new HttpHeaders();
        Journalpost jp1 = new Journalpost();
        List<Journalpost> JPtester = new ArrayList<>();
        JPtester.add(jp1);

        List <Journalposttype> jpts = new ArrayList<>();
        Journalposttype JT1 = Journalposttype.N;
        jpts.add(JT1);
        List <Journalstatus> jptts = new ArrayList<>();
        jptts.add(Journalstatus.MOTTATT);
        List<Tema>tt = new ArrayList<>();

        tt.add(Tema.AAP);
        BrukerIdInput bIdInput= new BrukerIdInput("001",BrukerIdType.FNR);
        SokJournalpostList_DTO brukerId = new SokJournalpostList_DTO(bIdInput, "2024-12-12", "2025-12-12", jpts, jptts, tt);
        Dokumentoversikt  dO = new Dokumentoversikt();
        ReturnFromGraphQl_DTO fgqlTest = new ReturnFromGraphQl_DTO(dO, "hello world");
        Mono <ReturnFromGraphQl_DTO> MfgglTest = Mono.just(Objects.requireNonNull(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).contentType(MediaType.APPLICATION_JSON).body(fgqlTest).getBody()));
        Mockito.when(serviceMock.hentJournalpostListe(any(SokJournalpostList_DTO.class), any(HttpHeaders.class))).thenReturn(MfgglTest); //headers and stuff dont get sendt, thats why error is getting there


        headers.add("Authorization", "Bearer ");
        String res = String.valueOf(safController.hentJournalpostListe(brukerId, headers));
        String cmp = String.valueOf(Mono.just(ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json").header("Content-Disposition", "inline").body(fgqlTest)));
        assertEquals("MonoOnErrorResume",res );
    }

    @SneakyThrows
    @Test
    public void hentDokumenterTest() {

//        String pathToRes = resources.toFile().getPath();
        String dokumentId = "000001";
        String journalpostId = "0000002";
        HttpHeaders headers = new HttpHeaders();
        byte[] fakePDF = "Cool pdfs".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource fakePDFresource = new ByteArrayResource(fakePDF);

        Mockito.when(serviceMock.hentDokument(dokumentId, journalpostId, headers)).thenReturn(Mono.just(fakePDFresource));
        String cmp = String.valueOf(Mono.just(ResponseEntity.status(HttpStatus.OK).headers(headers).contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document.pdf\"").body(fakePDFresource)).block());
        String res = String.valueOf(safController.hentDokument(dokumentId, journalpostId, headers).block());

        assertEquals(cmp, res);
    }


}