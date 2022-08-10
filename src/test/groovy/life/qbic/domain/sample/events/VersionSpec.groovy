package life.qbic.domain.sample.events


import spock.lang.Specification

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class VersionSpec extends Specification {
  def "equals works for equal objects"() {
    given:
    Version v1_0 = Version.create(1, 5)
    Version other = Version.create(1, 5)
    expect:
    v1_0.equals(other)
  }

  def "equals works for non-equal objects"() {
    given:
    Version v1_0 = Version.create(1, 0)
    Version differentMinor = Version.create(1, 1)
    Version differentMajor = Version.create(2, 0)
    expect:
    !v1_0.equals(differentMinor)
    !v1_0.equals(differentMajor)
  }

  def "compare return -1 if object smaller than other object"() {
    given:
    def version = Version.create(1, 0)
    def largerMajor = Version.create(2, 0)
    def largerMinor = Version.create(1, 1)
    expect:
    version.compareTo(largerMajor) == -1
    version.compareTo(largerMinor) == -1
  }

  def "compare return 1 if object larger than other object"() {
    given:
    def version = Version.create(2, 1)
    def smallerMajor = Version.create(1, 0)
    def largerMajor = Version.create(2, 0)
    expect:
    version.compareTo(smallerMajor) == 1
    version.compareTo(largerMajor) == 1
  }

  def "compare return 0 if object equals other object"() {
    given:
    def version = Version.create(2, 1)
    def equalVersion = Version.create(2, 1)
    expect:
    version.equals(equalVersion)
    version.compareTo(equalVersion) == 0
  }

  def "to string is unchanged"() {
    expect:
    Version.create(1, 5).toString() == "1.5"
  }
}
