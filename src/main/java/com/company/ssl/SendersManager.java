package com.company.ssl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendersManager {
    ArrayList<Sender> senders = new ArrayList<>();
    ArrayList<String> errors = new ArrayList<>();

    public SendersManager(){}

    public boolean hasError() {
        return errors.isEmpty();
    }

    public boolean readSenders(String file) {
        boolean hasError = false;
        try(FileReader reader = new FileReader(file))
        {
            int i = 0;
            while (true) {
                String username = "", password = "";
                // читаем посимвольно
                int c = reader.read();
                if( c == '[') {

                    c = reader.read();
                    while (c != ',') {
                        if(c != ' ') username = username + (char) c;
                        c = reader.read();
                    }

                    c = reader.read();
                    while (c != ']') {
                        if(c != ' ') password = password + (char) c;
                        c = reader.read();
                    }

                    senders.add(new Sender(username, password));
                    i++;
                }

                if((c = reader.read()) == -1) {
                    break;
                }
            }
        }
        catch(IOException ex){
            errors.add(ex.getMessage());
            hasError = true;
        }
        return hasError;
    }

    public boolean sendMessages(ArrayList<String> toEmails, String subject, String text, List<File> files) {
        boolean hasError = false;
        int j = 0, numberSenders = senders.size();
        int i = 0, numberEmails = toEmails.size(), starNumberErrors = 0;
        final boolean[] Errors  = new boolean[senders.size()];
        for (int f = 0; f < Errors.length; f++) {
            Errors[f] = false;
        }
        while (i < numberEmails) {
     //    while (true) {

            try {
                senders.get(j).send(subject, text, toEmails.get(i), files);
                errors.add("На " + toEmails.get(i)+ " от " +senders.get(j).getUsername()+ " отправлено \n");
                starNumberErrors = 0;
            } catch (Exception e) {
                errors.add("НА " + toEmails.get(i)+ " ОТ " +senders.get(j).getUsername()+ " НЕ ОТПРАВЛЕНО! \n");
                Errors[j] = true;
                System.out.println(e.getMessage());
                errors.add(e.getMessage());
                hasError = true;
                i--;
            } finally {
                int f = 0;
                while (Errors[f]) {

                    if(f < Errors.length){ f++;}
                    if(f == Errors.length){
                        if(starNumberErrors > senders.size()*10) return true;
                        starNumberErrors++;
                        break;
                    }
                }

                j++;
                if(j >= numberSenders) j = 0;
            }

            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            i++;
        }
        return hasError;
    }

    public ArrayList<String> getErrors() {
        ArrayList<String> errorsOut = errors;
        errors = new ArrayList<>();
        return errorsOut;
    }
}
