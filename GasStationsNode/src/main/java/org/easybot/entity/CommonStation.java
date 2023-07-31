package org.easybot.entity;

import jakarta.persistence.*;

//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@MappedSuperclass
public abstract class CommonStation {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   Long id;
   protected String gasType;
   protected String price;
   protected String location;
   @ManyToOne
   @JoinColumn(name = "brand_id", referencedColumnName = "id")
   protected GasStationsBrands gasStationsBrands;

   public String getGasType()
   {
      return gasType;
   }

   public void setGasType(String gasType)
   {
      this.gasType = gasType;
   }

   public String getPrice()
   {
      return price;
   }

   public void setPrice(String price)
   {
      this.price = price;
   }

   public String getLocation()
   {
      return location;
   }

   public void setLocation(String location)
   {
      this.location = location;
   }

   public GasStationsBrands getGasStationsBrands()
   {
      return gasStationsBrands;
   }

   public void setGasStationsBrands(GasStationsBrands gasStationsBrands)
   {
      this.gasStationsBrands = gasStationsBrands;
   }
}
