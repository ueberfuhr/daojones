package de.ars.daojones.eclipse.jdt.ui.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import de.ars.daojones.eclipse.jdt.ui.Activator;
import de.ars.daojones.eclipse.jdt.ui.preferences.Constants;

/**
 * A JUL {@link Handler} writing output to the Console.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class ConsoleHandler extends Handler {
	
	private final MessageConsole console;
	private final MessageConsoleStream stream;
	
	private static final Logger logger = Logger.getLogger(ConsoleHandler.class.getName());

	/**
	 * Creates an instance.
	 * @param console the {@link MessageConsole}
	 */
	public ConsoleHandler(MessageConsole console) {
		super();
		this.console = console;
		this.stream = console.newMessageStream();
		this.stream.println("Welcome to the DaoJones Bean Builder Console");
		this.stream.println("============================================");
		this.stream.println();
		//this.stream.setActivateOnWrite(true);
	}
	/**
	 * @see Handler#close()
	 */
	@Override
	public void close() throws SecurityException {
		flush();
		try {
			stream.close();
		} catch (IOException e) {
			throw new SecurityException(e);
		} finally {
			ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[]{this.console});
		}
	}
	/**
	 * @see Handler#flush()
	 */
	@Override
	public void flush() {
		try {
			stream.flush();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Unable to flush stream to console!", e);
		}
	}
	private void checkLevel() {
		final IPreferenceStore pStore = Activator.getDefault().getPreferenceStore();
		setLevel(Level.parse(pStore.getString(Constants.PREF_WORKBENCH_CONSOLE_LEVEL.getLocalName())));
	}
	/**
	 * @see Handler#publish(LogRecord)
	 */
	@Override
	public void publish(LogRecord record) {
		checkLevel();
		if(getLevel().intValue()<= record.getLevel().intValue()) {
//			stream.setColor(
//				record.getLevel().intValue() > Level.INFO.intValue() 
//				? 
//				ColorDescriptor.createFrom(
//					record.getLevel().intValue() > Level.WARNING.intValue() 
//					? 
//					new RGB(255, 0, 0) 
//					: 
//					new RGB(255, 165, 0)
//				).createColor(Display.getCurrent())
//				: 
//				null
//			); 
			stream.print(SimpleDateFormat.getDateTimeInstance().format(new Date(record.getMillis())));
			stream.print(" ");
			stream.print(record.getLevel().getName());
			stream.print(" ");
			stream.println(record.getMessage());
			if(null != record.getThrown()) {
				record.getThrown().printStackTrace(new PrintStream(new OutputStream() {
					@Override
					public void write(int c) throws IOException {
						stream.write(c);
					}
				}));
			}
		}
	}
	
}
