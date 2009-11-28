/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.smoke

import org.spockframework.EmbeddedSpecification
import org.spockframework.util.SyntaxException

import spock.lang.*

/**
 * @author Peter Niederwieser
 */
class BuiltInMembersOfClassSpecification extends EmbeddedSpecification {
  def "can be referred to by simple name"() {
    when:
    runner.run """
class Foo extends spock.lang.Specification {
  def foo() {
    when:
    throw new Exception()

    then:
    thrown(Exception)
  }
}
    """

    then:
    noExceptionThrown()
  }

  def "can be referred to by 'this' and 'super'"() {
    when:
    runner.run """
class Foo extends spock.lang.Specification {
  def foo() {
    def list = ${target}.Mock(List)

    expect:
    list != null
  }
}
    """

    then:
    noExceptionThrown()

    where:
    target << ["this", "super"]
  }

  @Issue("http://issues.spockframework.org/detail?id=43")
  def "can be used in field initializers"() {
    when:
    runner.run """
class Foo extends spock.lang.Specification {
  def list = Mock(List)

  def foo() { expect: true }
}
    """

    then:
    notThrown(SyntaxException)
  }
}