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
 * Copyright 2002 University of Waikato
 */

package weka_predictions.classifiers.rules;

import weka_predictions.classifiers.AbstractClassifierTest;
import weka_predictions.classifiers.Classifier;
import weka_predictions.classifiers.rules.DecisionTable;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests DecisionTable. Run from the command line with:<p>
 * java weka.classifiers.rules.DecisionTableTest
 *
 * @author <a href="mailto:eibe@cs.waikato.ac.nz">Eibe Frank</a>
 * @version $Revision: 8034 $
 */
public class DecisionTableTest extends AbstractClassifierTest {

  public DecisionTableTest(String name) { super(name);  }

  /** Creates a default DecisionTable */
  public Classifier getClassifier() {
    return new DecisionTable();
  }

  public static Test suite() {
    return new TestSuite(DecisionTableTest.class);
  }

  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }

}
