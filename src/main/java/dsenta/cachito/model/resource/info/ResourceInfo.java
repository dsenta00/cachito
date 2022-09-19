package dsenta.cachito.model.resource.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dsenta.cachito.factory.resourceinfo.ResourceInfoFactory;
import dsenta.cachito.model.clazz.Clazz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceInfo implements Comparable<ResourceInfo>, Serializable {
    private static final long serialVersionUID = -7177632502871432402L;
    private String name;
    private String key;
    private String fileName;
    @JsonIgnore
    private Clazz clazz;

    @Override
    public int hashCode() {
        return getOriginalFileName().hashCode();
    }

    @Override
    public int compareTo(ResourceInfo o) {
        return getOriginalFileName().compareTo(o.getOriginalFileName());
    }

    public String getOriginalFileName() {
        return ResourceInfoFactory.generateFileName(this);
    }

    @Override
    public String toString() {
        return getName();
    }
}