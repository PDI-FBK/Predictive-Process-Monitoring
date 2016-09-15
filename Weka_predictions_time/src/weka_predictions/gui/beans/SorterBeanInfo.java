/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    SorterBeanInfo.java
 *    Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka_predictions.gui.beans;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * BeanInfo class for the Sorter step
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision: 8051 $
 */
public class SorterBeanInfo extends SimpleBeanInfo {
  
  /**
   * Returns the event set descriptors
   *
   * @return an <code>EventSetDescriptor[]</code> value
   */
  public EventSetDescriptor [] getEventSetDescriptors() {
    try {
      EventSetDescriptor [] esds = 
      {       
          new EventSetDescriptor(DataSource.class, 
              "instance", 
              InstanceListener.class, 
          "acceptInstance"),
          new EventSetDescriptor(DataSource.class, 
              "dataSet", 
              DataSourceListener.class, 
          "acceptDataSet")
      };
      return esds;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }
  
  /**
   * Get the bean descriptor for this bean
   *
   * @return a <code>BeanDescriptor</code> value
   */
  public BeanDescriptor getBeanDescriptor() {
    return new BeanDescriptor(Sorter.class,
                              SorterCustomizer.class);
  }
}
