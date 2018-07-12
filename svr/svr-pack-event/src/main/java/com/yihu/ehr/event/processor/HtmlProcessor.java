package com.yihu.ehr.event.processor;

import com.yihu.ehr.event.model.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by progr1mmer on 2018/7/11.
 */
public class HtmlProcessor extends Processor {

    public HtmlProcessor(String type) {
        super(type);
    }

    @Override
    public void process(Event event) {
        if (active) {
            BufferedInputStream bufferedInputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(new File("C:\\Users\\progr1mmer\\Desktop\\非结构化\\5rYU7hwN - document\\documents\\73326.html")));
                byte [] fileBuffer = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(fileBuffer);
                String html = new String(fileBuffer, "GBK");
                //System.out.println(html);
                String patient_name = "";
                String dept_name = "";
                String bed_no = "";
                String hospital_number = "";
                Document document = Jsoup.parse(html);
                Elements header = document.select("header");
                if (header.hasText()) {
                    //System.out.println(header.html());
                    Elements p = header.get(0).select("p");
                    for (int i = 0; i < p.size(); i ++) {
                        Elements span = p.get(i).select("span");
                        if (span.hasText()) {
                            int j = 0;
                            while (j < span.size()) {
                                Element element = span.get(j);
                                if (element.text().contains("姓名")) {
                                    patient_name = element.nextElementSibling().text();
                                }
                                if (element.text().contains("科室")) {
                                    dept_name = element.nextElementSibling().text();
                                }
                                if (element.text().contains("床号")) {
                                    bed_no = element.nextElementSibling().text();
                                }
                                if (element.text().contains("住院号")) {
                                    hospital_number = element.nextElementSibling().text();
                                }
                                j ++;
                            }
                        }
                    }
                }
                System.out.println(patient_name);
                System.out.println(dept_name);
                System.out.println(bed_no);
                System.out.println(hospital_number);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String [] args) {
        Processor processor = new HtmlProcessor("Resolve");
        processor.process(new Event("Resolve", "{}"));
    }

}
