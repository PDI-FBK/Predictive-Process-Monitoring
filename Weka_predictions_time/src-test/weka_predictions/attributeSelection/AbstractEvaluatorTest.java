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
 * Copyright (C) 2006 University of Waikato 
 */

package weka_predictions.attributeSelection;

import weka_predictions.attributeSelection.CheckAttributeSelection;
import weka_predictions.core.CheckGOE;
import weka_predictions.core.CheckOptionHandler;
import weka_predictions.core.OptionHandler;
import weka_predictions.core.CheckScheme.PostProcessor;

/**
 * Abstract Test class for evaluator. Internally it uses the
 * class <code>CheckAttributeSelection</code> to determine success or failure
 * of the tests. It follows basically the <code>testsPerClassType</code>
 * method.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8034 $
 *
 * @see CheckAttributeSelection
 * @see CheckAttributeSelection#testsPerClassType(int, boolean, boolean)
 * @see PostProcessor
 */
public abstract class AbstractEvaluatorTest 
  extends AbstractAttributeSelectionTest {
  
  /**
   * Constructs the <code>AbstractEvaluatorTest</code>. Called by subclasses.
   *
   * @param name the name of the test class
   */
  public AbstractEvaluatorTest(String name) { 
    super(name); 
  }
  
  /**
   * configures the CheckAttributeSelection instance used throughout the tests
   * 
   * @return	the fully configured CheckAttributeSelection instance used for testing
   */
  protected CheckAttributeSelection getTester() {
    CheckAttributeSelection	result;
    
    result = super.getTester();
    result.setTestEvaluator(true);
    
    return result;
  }
  
  /**
   * Configures the CheckOptionHandler uses for testing the optionhandling.
   * Sets the scheme to test.
   * 
   * @return	the fully configured CheckOptionHandler
   */
  protected CheckOptionHandler getOptionTester() {
    CheckOptionHandler		result;
    
    result = super.getOptionTester();
    if (getEvaluator() instanceof OptionHandler)
      result.setOptionHandler((OptionHandler) getEvaluator());
    
    return result;
  }
  
  /**
   * Configures the CheckGOE used for testing GOE stuff.
   * 
   * @return	the fully configured CheckGOE
   */
  protected CheckGOE getGOETester() {
    CheckGOE		result;

    result = super.getGOETester();
    result.setObject(getEvaluator());
    
    return result;
  }
}
