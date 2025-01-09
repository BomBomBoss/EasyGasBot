package org.easybot.entity.stations;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.easybot.entity.GasStationsBrands;

@MappedSuperclass
@Setter
@Getter
@ToString
@EqualsAndHashCode(exclude = "id")
public abstract class CommonStation implements Comparable <CommonStation> {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   Long id;
   public String gasType;
   public String price;
   public String location;
   @ManyToOne
   @JoinColumn(name = "brand_id", referencedColumnName = "id")
   protected GasStationsBrands gasStationsBrands;

   @Override
   public int compareTo(CommonStation station)
   {
      double price1 = Double.parseDouble(price);
      double price2 = Double.parseDouble(station.getPrice());

      return Double.compare(price1, price2);
   }

}
