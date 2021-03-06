package com.staff.api.entity;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.sql.Date;

public class Candidate implements IEntity<Candidate>{
    public enum CandidateState {
        ARCHIVE("В архиве"), ACTIVE("Активен");
        private String description;
        private CandidateState(String description) {
            this.description = description;
        }
        public String getDescription() {return description;}
    }

    private Integer id;
    private String name;
    private String surname;
    private double salary;
    private Date birthday;
    private CandidateState candidateState;
    private List<ContactDetails> contactDetailsList;

    public boolean isNew() {
        return (this.id == null);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public double getSalary() {
        return salary;
    }

    public Date getBirthday() {
        return birthday;
    }

    public CandidateState getCandidateState() {
        return candidateState;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    //public void setBirthday(Date birthday) {
    //    this.birthday = birthday;
    //}

    public void setBirthday(String birthday) {
        String[] validPatterns = {"dd.MM.yyyy","dd/MM/yyyy","dd-MM-yyyy","dd/mm/yy","yyyy-MM-dd"};
        SimpleDateFormat formatter = new SimpleDateFormat();
        for (int i=0; i<validPatterns.length;i++) {
            try {
                formatter.applyPattern(validPatterns[i]);
                formatter.setLenient(false);
                //this.birthday = formatter.parse(birthday);
                this.birthday = new java.sql.Date((formatter.parse(birthday)).getTime());
                return;
            } catch (ParseException e) {
                // nothing to do
            }
        }
    }
    public void setCandidateState(String st) {
        CandidateState[] states = CandidateState.values();

        for(CandidateState c : states) {
            if (st.equals(c.getDescription())) {
                this.candidateState = c;
            }
        }
    }

    public void setCandidateState(CandidateState candidateState) {
        this.candidateState = candidateState;
    }

    public List<ContactDetails> getContactDetailsList() {
        return contactDetailsList;
    }

    public void setContactDetailsList(List<ContactDetails> contactDetailsList) {
        this.contactDetailsList = contactDetailsList;
    }

    public String getBirthdayAsString() {
        if (!(this.birthday == null)) {
            Format formatter = new SimpleDateFormat("dd.MM.yyyy");
            return (formatter.format(birthday));
        } else {
            return ("");
        }
        //return String.format("%td.%tm.%tY",birthday,birthday,birthday);
    }

    // get a string like '01.03.2015', parse it and set birthday of Candidate
    /*public void setBirthday(String s) {

        if (s == null) {
            return;
        }
        try {
            int day = Integer.parseInt(s.substring(0, 2));
            int month = Integer.parseInt(s.substring(3, 5))-1;
            int year = Integer.parseInt(s.substring(6, 10));
            birthday.set(year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
            birthday.set(1900, Calendar.JANUARY, 1);
        }
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return id == candidate.id &&
                Double.compare(candidate.salary, salary) == 0 &&
                Objects.equals(name, candidate.name) &&
                Objects.equals(surname, candidate.surname) &&
                Objects.equals(birthday, candidate.birthday) &&
                candidateState == candidate.candidateState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, salary, birthday, candidateState);
    }

    @Override
    public String toString() {
        return "Candidate [" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", salary=" + salary +
                ", birthday=" + getBirthdayAsString() +
                ", candidateState=" + candidateState +
                "]";
    }

    @Override
    public void setForeignKey(String foreignKey) {
        this.id = Integer.parseInt(foreignKey);
    }

    @Override
    public String getForeignKey() {
        /*TODO: */
        return this.id != null ? this.id.toString() : "-1";
    }
}
