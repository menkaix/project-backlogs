package dashboard.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import dashboard.data.values.LifeCycle;

@Entity
public class ProjectStatus {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String statusName ;
	private String description ;
	private LifeCycle cycle ;

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName.toUpperCase();
	}

	public LifeCycle getCycle() {
		return cycle;
	}

	public void setCycle(LifeCycle cycle) {
		this.cycle = cycle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
