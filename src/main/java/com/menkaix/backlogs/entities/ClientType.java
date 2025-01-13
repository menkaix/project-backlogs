package dashboard.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ClientType {

	@Id

	private Integer id;

	private String clientTypeName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClientTypeName() {
		return clientTypeName;
	}

	public void setClientTypeName(String clientTypeName) {
		this.clientTypeName = clientTypeName;
	}

}
