package dsenta.cachito.model.user;

import dsenta.cachito.model.schema.Schema;
import dsenta.queryablemap.trie.Trie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = -4933687621477068444L;
    private String username;
    private String password;
    private Map<String, Schema> schemaMap = new Trie<>();

    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
    }
}