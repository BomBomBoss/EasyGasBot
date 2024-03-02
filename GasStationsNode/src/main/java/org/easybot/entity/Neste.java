package org.easybot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.repository.cdi.Eager;

@Entity
@Table(name = "neste")
public class Neste extends CommonStation {

}
