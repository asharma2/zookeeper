package com.knowledge.zookeeper.model;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;

public class Movie extends ZooId {

	private String name;
	private Date releasedOn;
	private Set<String> actors = Sets.newHashSet();
	private int budget;
	private String category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getReleasedOn() {
		return releasedOn;
	}

	public void setReleasedOn(Date releasedOn) {
		this.releasedOn = releasedOn;
	}

	public Set<String> getActors() {
		return actors;
	}

	public void setActors(Set<String> actors) {
		this.actors = actors;
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Movie [name=" + name + ", releasedOn=" + releasedOn + ", actors=" + actors + ", budget=" + budget
				+ ", category=" + category + ", id=" + id + "]";
	}

}
