package dashboard.data.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TimeLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne
	private Task task ;
	
	@ManyToOne
	private People owner ;
	
	private String mailSubject ;
	
	public People getOwner() {
		return owner;
	}
	public void setOwner(People owner) {
		this.owner = owner;
	}
	private Float seconds ;
	
	private Date creationDate ;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	
	public Float getSeconds() {
		return seconds;
	}
	public void setSeconds(Float seconds) {
		this.seconds = seconds;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
}
