package model;

import java.io.Serializable;
import java.util.Objects;

public class BaseEntity implements Serializable {
    protected String idFs;
    public void setIdFs(String idFs){
        this.idFs=idFs;
    }
    public String getIdFs(){
        return idFs;
    }
    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(getIdFs(),that.getIdFs());
    }
    public BaseEntity(){}
}
