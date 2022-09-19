package dsenta.cachito.model.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.user.User;
import dsenta.queryablemap.trie.Trie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schema implements Serializable {
    private static final long serialVersionUID = 5713874112426472345L;
    private String name;
    @JsonIgnore
    private User owner;
    @JsonIgnore
    private Map<String, User> userAccess = new Trie<>();
    private Map<String, Clazz> clazzMap = new Trie<>();

    public Schema(String name, User owner) {
        setName(name);
        setOwner(owner);
        getUserAccess().put(owner.getUsername(), owner);
    }

    public Schema(String name) {
        setName(name);
    }
}