package strat.mining.stratum.proxy.grizzly;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A CLStaticHttpHandler which looks for the file index.html if the root is
 * requested. This HttpHandler also set the charset option of the content type
 * header of responses (if the response content is text).
 * 
 */
public class CLStaticHttpHandlerWithIndexSupport extends CLStaticHttpHandler {

	private final Logger SUB_LOGGER = LoggerFactory.getLogger(CLStaticHttpHandler.class);

	public CLStaticHttpHandlerWithIndexSupport(ClassLoader classLoader, String... docRoots) {
		super(classLoader, docRoots);
	}

	@Override
	protected boolean handle(String resourcePath, Request request, Response response) throws Exception {
		SUB_LOGGER.trace("Requested resource: {}.", resourcePath);

		long time = System.currentTimeMillis();
		String resourcePathFiltered = resourcePath;
		// If the root is requested, then replace the
		// requested resource by index.html
		if ("/".equals(resourcePath)) {
			resourcePathFiltered = "/index.html";
		}

		// Set the content type before calling the
		// super.handle method to set the character
		// encoding. Since the content type will already be
		// set, the super.handle method will not override
		// this content type.
		setContentType(response, resourcePathFiltered);

		boolean found = super.handle(resourcePathFiltered, request, response);

		time = System.currentTimeMillis() - time;
		if (found) {
			SUB_LOGGER.trace("Resource sent in {} ms: {}.", time, resourcePath);
		} else {
			SUB_LOGGER.trace("Resource not found: {}.", resourcePath);
		}
		return found;
	}

	/**
	 * Set the content type (with charset) in the given response header. The
	 * content type is guessed from the requested file extension.
	 * 
	 * @param response
	 * @param resourcePath
	 */
	private void setContentType(Response response, String resourcePath) {
		pickupContentType(response, resourcePath);
		if (response.getContentType() != null && response.getContentType().startsWith("text/")) {
			response.setCharacterEncoding("UTF-8");
		}
	}

}
