/*
 *   Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 *
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package hu.sonrisa.backend.async;

import hu.sonrisa.backend.AppContextProvider;
import hu.sonrisa.backend.auditlog.AuditLogService;
import hu.sonrisa.backend.mail.EmailService;
import hu.sonrisa.backend.model.ResourceBasedUzenet;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Egy háttérben futtatandó, külön Thread-et igénylő folyamat. mely képes
 * jelezni, ha véget ért és üzenetek megosztására képes a folyamatot monitorozó
 * külső szemlélővel (UI)
 *
 * @author sonrisa
 */
public abstract class HatterFolyamat implements Runnable {

    /**
     * Az üzenet ami kivétel esetén az üzenetek közé íródik
     */
    public static final String EXCEPTION_KEY = "exception.unknown";
    private static final Logger LOGGER = LoggerFactory.getLogger(HatterFolyamat.class);
    private LinkedList<ResourceBasedUzenet> uzenetek = new LinkedList<ResourceBasedUzenet>();
    private boolean finished = false;
    private boolean interrupted = false;
    private boolean resultSaved = false;
    private static final HatterFolyamatRepository repository = new HatterFolyamatRepository();
    private String id;
    private String futtato;
    private Date indulas;
    /**
     * Háttérfolyamat futása során keletkezett exception
     */
    private Exception exception;

    /**
     * Konstruktor
     */
    public HatterFolyamat() {
        id = UUID.randomUUID().toString();
        indulas = new Date();
        repository.add(this);
    }

    public static HatterFolyamat getFolyamat(String id) {
        return repository.get(id);
    }

    /**
     * Folyamat leállítása azonosító alapján
     *
     * @param id
     * @return
     */
    public static boolean stopFolyamat(String id) {
        HatterFolyamat hf = repository.get(id);
        if (hf != null) {
            hf.interrupted = true;
            return true;
        }
        return false;
    }

    public boolean isResultSaved() {
        return resultSaved;
    }

    public void setResultSaved(boolean resultSaved) {
        this.resultSaved = resultSaved;
        if (resultSaved) {
            repository.remove(id);
        }
    }

    /**
     * Folyamat azonosítója
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * A folyamatot futtató felhasználó azonosítója
     *
     * @return
     */
    public String getFuttato() {
        return futtato;
    }

    /**
     * A folyamatot futtató felhasználó azonosítójának beállítása
     *
     * @param futtato
     */
    public void setFuttato(String futtato) {
        this.futtato = futtato;
    }

    /**
     * A folyamat elindításának dátuma
     *
     * @return
     */
    public Date getIndulas() {
        return indulas;
    }

    /**
     * Üzenet hozzáadása
     *
     * @param messageKey
     * @param params
     */
    public void addUzenet(String messageKey, Object... params) {
        addUzenet(new ResourceBasedUzenet(messageKey, params));
    }

    /**
     * Add uzenet
     *
     * @param uzenet
     */
    public synchronized void addUzenet(ResourceBasedUzenet uzenet) {
        uzenetek.add(0, uzenet);
    }

    /*
     public synchronized void addUzenet(ResourceBasedUzenet uzenet) {
     uzenetek.add(0, uzenet);
     }
     */
    /**
     * @param messageKey
     * @param params
     */
    public synchronized void changeLastUzenet(String messageKey, Object... params) {
        if (uzenetek.size() > 0) {
            uzenetek.removeFirst();
        }
        addUzenet(messageKey, params);
    }

    /**
     * Az eddigi üzenetek visszaadása. Időrendben csökkenve jelennek majd meg
     * sortöréssel elválasztva az egyes üzenetek.
     *
     * @return
     */
    public synchronized List<ResourceBasedUzenet> getUzenetek() {
        return new ArrayList<ResourceBasedUzenet>(uzenetek);
    }

    /**
     * A folyamat futásának indítása
     */
    @Override
    public final void run() {
        try {
            LOGGER.debug("Folyamat indul: " + this.toString());
            futtatas();
        } catch (Exception ex) {
            try {
                addUzenet(EXCEPTION_KEY, ex.toString());
                LOGGER.error("", ex);
                StringWriter s = new StringWriter();
                ex.printStackTrace(new PrintWriter(s));
                AppContextProvider.getContext().getBean(AuditLogService.class).log("HIBA", ex.getClass().getName() + ": " + ex.getMessage() + "\n" + s.toString());
                handleException(ex);
            } catch (Exception ex2) {
                LOGGER.error("", ex2);
            }
            exception = ex;
        } finally {
            finished = true;
            onFinished();
            LOGGER.debug("Folyamat leállt: " + this.toString());
        }
    }

    /**
     * Felüldefiniálandó metódus, mely a valódi folyamatot jelenti
     */
    protected abstract void futtatas();

    /**
     * @return Visszaadja, hogy az adott folyamat futása megszakítható-e?
     */
    public abstract boolean isMegszakithato();

    /**
     * Befejeződött már a folyamat?
     *
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Megszakítja a folyamat futását (beállítja az interrupted flag-et
     * true-ra).
     */
    public void megszakit() {
        this.interrupted = true;
        addUzenet("folyamat.megszakit");
    }

    /**
     * Meg lett-e kívülről szakítva a folyamat futás közben
     *
     * @return
     */
    public boolean isMegszakitott() {
        return interrupted;
    }

    /**
     *
     * @param ex
     */
    protected final void handleException(Exception ex) {
        EmailService emailService = AppContextProvider.getContext().getBean(EmailService.class);
        if (emailService != null) {
            emailService.sendExceptionMail("mail.exception.text", ex, null);
        } else {
            LOGGER.error("No e-mail sent due to email service not available");
        }
    }

    /**
     * Keletkezett-e a folyamat futása során exception
     */
    public boolean voltException() {
        return exception != null ? true : false;
    }

    /**
     * A folyamat futása során keletkezett hibaüzenet.
     */
    public Exception getException() {
        return exception;
    }

    protected void onFinished() {
    }
}
