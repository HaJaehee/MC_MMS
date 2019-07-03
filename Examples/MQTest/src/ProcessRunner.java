import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.lang3.SystemUtils;

public class ProcessRunner { 

    public static void main(String args[])
                throws IOException,    InterruptedException {
    	String[] command = {""};
    	if (SystemUtils.IS_OS_LINUX) {
    		command = new String[] { "./rabbitmq-binary/rabbitmqadmin", "list", "queues", "vhost", "name" };
		}
    	else if (SystemUtils.IS_OS_WINDOWS) {
    		command = new String[] { "python.exe", "./rabbitmq-binary/rabbitmqadmin", "list", "queues", "vhost", "name" };
    	}
    	
        ProcessRunner runner = new ProcessRunner();
        StringBuilder processOutput = runner.byRuntime(command);
        System.out.println(processOutput.toString());
        System.out.println(getTotalQueueNumberFromProcOutput (processOutput.toString()) );
        
    }
    
	private static long getTotalQueueNumberFromProcOutput (String processOutput) {
		
		if (processOutput.equals("No items")) {
			return 0;
		}
		else {
			int count = 0;
			for (int i = 0; i < processOutput.length(); i++) {
			    if (processOutput.charAt(i) == '\n') {
			        count++;
			    }
			}
			return count-3;
			// Remove negligible lines.
		}
	}
	
    private StringBuilder byRuntime(String[] command)
            throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);
        return printStream(process);
    }

    private StringBuilder printStream(Process process)
                throws IOException, InterruptedException {
        process.waitFor();
        StringBuilder ret = new StringBuilder();
        OutputStream ost = new OutputStream() {
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				// TODO Auto-generated method stub
				ret.append(new String(b).trim());
			}

			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				
			}
		};
        try (InputStream psout = process.getInputStream()) {
            copy(psout, ost);
        }
        return ret;
    }

    private void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
    }
    
}