package org.easybot.entity;

import jakarta.persistence.*;

import java.util.Objects;

@MappedSuperclass
public abstract class CommonStation implements Comparable <CommonStation> {
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

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (!(o instanceof CommonStation station)) return false;

      if (!Objects.equals(gasType, station.gasType)) return false;
      if (!Objects.equals(price, station.price)) return false;
      if (!Objects.equals(location, station.location)) return false;
      return Objects.equals(gasStationsBrands, station.gasStationsBrands);
   }

   @Override
   public int hashCode()
   {
      int result = gasType != null ? gasType.hashCode() : 0;
      result = 31 * result + (price != null ? price.hashCode() : 0);
      result = 31 * result + (location != null ? location.hashCode() : 0);
      result = 31 * result + (gasStationsBrands != null ? gasStationsBrands.hashCode() : 0);
      return result;
   }

   @Override
   public int compareTo(CommonStation station)
   {
      double price1 = Double.parseDouble(price);
      double price2 = Double.parseDouble(station.getPrice());

      return Double.compare(price1, price2);
   }

   @Override
   public String toString()
   {
      return "CommonStation{" +
              "gasType='" + gasType + '\'' +
              ", price='" + price + '\'' +
              ", location='" + location + '\'' +
              ", gasStationsBrands=" + gasStationsBrands +
              '}';
   }
}
