package org.easybot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public abstract class CommonStation {
   @Id
   Long id;
   protected String gasType;
   protected String price;
   protected String location;
}
