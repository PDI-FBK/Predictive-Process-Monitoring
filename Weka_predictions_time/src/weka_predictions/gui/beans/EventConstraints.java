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
 *    EventConstraints.java
 *    Copyright (C) 2002-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka_predictions.gui.beans;


/**
 * Interface for objects that want to be able to specify at any given
 * time whether their current configuration allows a particular event
 * to be generated.
 *
 * @author <a href="mailto:mhall@cs.waikato.ac.nz">Mark Hall</a>
 * @version $Revision: 8034 $
 */
public interface EventConstraints {
  
  /**
   * Returns true if, at the current time, the named event could be
   * generated.
   *
   * @param eventName the name of the event in question
   * @return true if the named event could be generated
   */
  boolean eventGeneratable(String eventName);
}
