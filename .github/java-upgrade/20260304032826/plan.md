# Java Upgrade Plan

## Metadata
- **Session ID**: 20260304032826
- **Project Name**: kudafocus
- **Project Path**: /Users/hjiang/Developer/kudafocus
- **Current Java Version**: 11
- **Target Java Version**: 21
- **Plan Generated**: 2026-03-04
- **Working Branch**: appmod/java-upgrade-20260304032826

## Objectives
- Upgrade Java runtime from 11 to 21 (LTS)
- Ensure all production code compiles with Java 21
- Ensure all tests pass with Java 21 (100% pass rate)
- Update Maven tooling for optimal Java 21 support

## Options
- Run tests before and after the upgrade: true
- Intermediate versions: none required (direct upgrade Java 11 → 21 is safe)

## Guidelines
(None specified by user)

## Available Tools

| Tool | Version | Path | Usage |
|---|---|---|---|
| **JDK (Current)** | 11.x | System default | Step 2 baseline |
| **JDK (Target)** | 21.0.7 | /Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home | Steps 3-5 upgrade |
| **Maven** | 3.9.12 | /opt/homebrew/Cellar/maven/3.9.12/bin | Build tool (all steps) |
| **Maven Wrapper** | Not available | N/A | Not used |

## Technology Stack

| Dependency | Current Version | Java 11 Status | Java 21 Status | EOL | Notes |
|---|---|---|---|---|---|
| JavaFX Controls | 23.0.1 | ✓ Supported | ✓ Supported | No | Latest stable, fully compatible |
| JavaFX Graphics | 23.0.1 | ✓ Supported | ✓ Supported | No | Latest stable, fully compatible |
| JavaFX Base | 23.0.1 | ✓ Supported | ✓ Supported | No | Latest stable, fully compatible |
| Gson | 2.10.1 | ✓ Supported | ✓ Supported | No | Stable JSON serialization library |
| JUnit Jupiter | 5.9.3 | ✓ Supported | ✓ Supported | No | Modern testing framework |
| Maven Compiler Plugin | 3.10.1 | ✓ Supported | ✓ Supported | No | Supports Java 21 |
| JavaFX Maven Plugin | 0.0.8 | ✓ Supported | ✓ Supported | No | Supports Java 21 module paths |
| Maven Surefire Plugin | 2.22.2 | ✓ Supported | ⚠ Legacy | No | Legacy; should upgrade for better Java 21 support |

## Derived Upgrades

| Dependency | Current | Target | Reason | Risk |
|---|---|---|---|---|
| Maven Surefire Plugin | 2.22.2 | 3.0.0-M9 | Modern Java 21 support, JVM module system integration | Low - backward compatible |

No other dependencies require upgrading. JavaFX 23.0.1 is already at latest stable version compatible with Java 21.

## Key Challenges

1. **Maven Surefire Plugin Version**: Current version 2.22.2 is legacy and may have compatibility issues with Java 21's module system. **Mitigation**: Upgrade to 3.0.0-M9 which has full Java 21 support and improved JVM integration.

2. **JavaFX Runtime Modules**: JavaFX 23 requires proper module path configuration on Java 21. **Mitigation**: JavaFX Maven Plugin 0.0.8 correctly handles module paths; no changes needed.

3. **No Breaking Changes Expected**: Direct jump from Java 11 (LTS) to Java 21 (LTS) is safe—both are stable releases with mature APIs. No deprecated APIs used in the project.

## Upgrade Steps

### Step 1: Setup Environment
**Objective**: Verify Java 21 JDK availability and Maven tooling  
**JDK to Use**: Java 11 (current, for verification only)

**Changes to Make**:
- Verify Java 21.0.7 JDK is accessible at `/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home`
- Verify Maven 3.9.12 is functional

**Verification Command**:
```bash
java -version && mvn --version
```

**Expected Result**: 
- Java 21.0.7 confirmed available
- Maven 3.9.12 confirmed and ready

---

### Step 2: Setup Baseline
**Objective**: Establish baseline compilation and test results with current Java 11  
**JDK to Use**: Java 11 (current)

**Changes to Make**:
- No code changes; baseline measurement only
- Run full compile and test cycle

**Verification Commands**:
```bash
mvn clean test-compile
mvn clean test
```

**Expected Result**:
- Compilation succeeds with Java 11
- All tests pass with Java 11
- Baseline metrics documented for comparison

---

### Step 3: Update Java Compiler Version in pom.xml  
**Objective**: Update Maven compiler plugin to target Java 21  
**JDK to Use**: Java 21.0.7

**Changes to Make**:
1. Update property `<maven.compiler.source>11</maven.compiler.source>` → `<maven.compiler.source>21</maven.compiler.source>`
2. Update property `<maven.compiler.target>11</maven.compiler.target>` → `<maven.compiler.target>21</maven.compiler.target>`
3. Update maven-compiler-plugin `<source>11</source>` → `<source>21</source>`
4. Update maven-compiler-plugin `<target>11</target>` → `<target>21</target>`

**Verification Command**:
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
mvn clean test-compile
```

**Expected Result**:
- Production code compiles successfully with Java 21
- No compilation errors or warnings
- All test classes compile

---

### Step 4: Upgrade Maven Surefire Plugin
**Objective**: Update test runner plugin for optimal Java 21 compatibility  
**JDK to Use**: Java 21.0.7

**Changes to Make**:
- Update maven-surefire-plugin version from `2.22.2` to `3.0.0-M9`

**Verification Command**:
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
mvn clean test
```

**Expected Result**:
- All tests compile and run with Java 21
- 100% test pass rate (match or exceed baseline)
- Test runner properly handles Java 21 module system

---

### Step 5: Final Validation
**Objective**: Verify all upgrade goals are met and project is production-ready  
**JDK to Use**: Java 21.0.7

**Changes to Make**:
- None; validation only
- Verify all configuration changes from steps 3-4 are in place
- Run full build and verify lifecycle

**Verification Command**:
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
mvn clean verify
```

**Expected Result**:
- ✓ Compilation succeeds for all source and test code
- ✓ All tests pass (100% pass rate)
- ✓ Build artifacts generated successfully
- ✓ Project is ready for Java 21 deployment

---

## Plan Review

**Status**: ✓ Plan is complete and feasible

**Review Notes**:
- All dependencies are compatible with Java 21; no blocking issues identified
- Direct upgrade approach (Java 11 → 21) is safe; no intermediate versions needed
- Maven Surefire Plugin upgrade is recommended for optimal Java 21 support
- JavaFX 23.0.1 is latest stable version and fully supports Java 21
- No deprecated APIs detected in project code
- Project has clean compilation and test baseline, ensuring accurate upgrade verification
- Estimated effort: Low (simple pom.xml updates, no code changes)
- Risk level: Low (stable LTS-to-LTS upgrade)

**Potential Limitations**: None identified
