package com.yihu.ehr.test;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by progr1mmer on 2018/6/22.
 */
public class SolrSchema {

    @Test
    public void test() {
        Connection conn = null;
        Statement sta1 = null;
        ResultSet result1 = null;
        try {
            File file = new File("E:/xml/schema_sub.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //Document doc = builder.newDocument();
            Document doc = builder.parse(file);
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://172.19.103.57:3306/db1?user=root&password=xmjkzl&useUnicode=true&characterEncoding=UTF-8");
            sta1 = conn.createStatement();
            result1 = sta1.executeQuery("SELECT * FROM rs_metadata WHERE std_code IN " +
                    "(SELECT inner_code FROM std_metadata_59083976eebd WHERE dataset_id IN " +
                    "(SELECT id FROM std_dataset_59083976eebd WHERE multi_record = 1)) " +
                    "AND dict_code IS NOT NULL AND LENGTH(dict_code) > 0");
            //Element root = doc.getElementById("schema");
            Node node = doc.getLastChild();
            //NodeList nodeList = doc.getChildNodes();
            //nodeList.getLength();
            //System.out.println(nodeList.getLength());
            while (result1.next()) {
                String columnType = result1.getString("column_type");
                if (columnType.startsWith("S")) {
                    Element src = doc.createElement("field");
                    src.setAttribute("name", result1.getString("id"));
                    src.setAttribute("type", "string");
                    src.setAttribute("indexed", "true");
                    src.setAttribute("stored", "false");
                    node.appendChild(doc.createTextNode("\n	"));
                    node.appendChild(src);
                    Element dict = doc.createElement("field");
                    dict.setAttribute("name", result1.getString("id") + "_VALUE");
                    dict.setAttribute("type", "string");
                    dict.setAttribute("indexed", "true");
                    dict.setAttribute("stored", "false");
                    node.appendChild(doc.createTextNode("\n	"));
                    node.appendChild(dict);
                    Element ansj = doc.createElement("field");
                    ansj.setAttribute("name", result1.getString("id") + "_VALUE_ANSJ");
                    ansj.setAttribute("type", "text_ansj");
                    ansj.setAttribute("indexed", "true");
                    ansj.setAttribute("stored", "false");
                    node.appendChild(doc.createTextNode("\n	"));
                    node.appendChild(ansj);
                    Element copy = doc.createElement("copyField");
                    copy.setAttribute("source", result1.getString("id") + "_VALUE");
                    copy.setAttribute("dest", result1.getString("id") + "_VALUE_ANSJ");
                    node.appendChild(doc.createTextNode("\n	"));
                    node.appendChild(copy);
                } else if (columnType.startsWith("D")) {
                    Element src = doc.createElement("field");
                    src.setAttribute("name", result1.getString("id"));
                    src.setAttribute("type", "date");
                    src.setAttribute("indexed", "true");
                    src.setAttribute("stored", "false");
                    node.appendChild(doc.createTextNode("\n	"));
                    node.appendChild(src);
                } else if (columnType.startsWith("L")) {
                    Element src = doc.createElement("field");
                    src.setAttribute("name", result1.getString("id"));
                    src.setAttribute("type", "string");
                    src.setAttribute("indexed", "true");
                    src.setAttribute("stored", "false");
                    node.appendChild(doc.createTextNode("\n	"));
                    node.appendChild(src);
                } else if (columnType.startsWith("N")) {
                    Element src = doc.createElement("field");
                    src.setAttribute("name", result1.getString("id"));
                    src.setAttribute("type", "double");
                    src.setAttribute("indexed", "true");
                    src.setAttribute("stored", "false");
                    node.appendChild(doc.createTextNode("\n	"));
                    node.appendChild(src);
                }
            }
            /**
             Element root = doc.createElement("schema");
             root.setAttribute("name", "HealthProfile");
             root.setAttribute("version", "1.5");
             //doc.appendChild(root);
             Element child1 = doc.createElement("field");
             child1.setAttribute("name", "long");
             root.appendChild(stu);
             Element stu_name = doc.createElement("姓名");
             stu.appendChild(stu_name);
             Text name_text = doc.createTextNode("罗文雯");
             stu_name.appendChild(name_text);
             Element stu_age = doc.createElement("年龄");
             stu.appendChild(stu_age);
             Text age_text = doc.createTextNode("25");
             stu_age.appendChild(age_text);
             */
            FileOutputStream fos = new FileOutputStream("E:/tryfile/xml/schema2.xml");
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            Source source = new DOMSource(doc);
            Result res = new StreamResult(osw);
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            xformer.transform(source, res);
            //callDomWriter(doc, osw, "UTF-8");
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if (result1 != null) {
                    result1.close();
                }
                if (sta1 != null) {
                    sta1.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
