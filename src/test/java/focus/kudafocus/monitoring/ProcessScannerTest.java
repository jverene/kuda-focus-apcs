package focus.kudafocus.monitoring;

import java.util.ArrayList;
import java.util.List;

public class ProcessScannerTest {
    private static class FakeMonitor extends AppMonitor {
        private List<ProcessInfo> list = new ArrayList<>();
        
        @Override 
        protected List<ProcessInfo> getCurrentProcesses() {
            return new ArrayList<>(list);
        }

        @Override 
        protected String normalizeProcessName(String raw) { return raw; }

        public void setProcesses(List<ProcessInfo> procs) { list = new ArrayList<>(procs); }
    }
}
