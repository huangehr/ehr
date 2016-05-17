package com.yihu.ehr.resource.model;

        import org.hibernate.annotations.GenericGenerator;

        import javax.persistence.*;

/**
 * 资源接口
 *
 * Created by lyr on 2016/5/13.
 */
@Entity
@Table(name="rs_interface")
public class RsInterface {
    private String id;
    private String name;
    private String resourceInterface;
    private String paramDescription;
    private String resultDescription;
    private String description;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name="name",nullable = false)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    @Column(name="resource_interface",nullable = false)
    public String getResourceInterface(){
        return resourceInterface;
    }
    public void setResourceInterface(String resourceInterface){
        this.resourceInterface = resourceInterface;
    }

    @Column(name="param_description")
    public String getParamDescription(){
        return paramDescription;
    }
    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    @Column(name="result_description")
    public String getResultDescription() {
        return resultDescription;
    }
    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    @Column(name="description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
