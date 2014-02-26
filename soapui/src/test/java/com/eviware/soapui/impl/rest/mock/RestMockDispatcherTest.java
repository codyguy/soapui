package com.eviware.soapui.impl.rest.mock;

import com.eviware.soapui.impl.wsdl.mock.WsdlMockRunContext;
import com.eviware.soapui.model.mock.MockRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import static org.mockito.Mockito.*;

public class RestMockDispatcherTest
{

	private HttpServletRequest request;
	private HttpServletResponse response;
	private RestMockDispatcher restMockDispatcher;
	private WsdlMockRunContext context;
	private RestMockService restMockService;

	@Before
	public void setUp()
	{
		createRestMockDispatcher();
	}

	@Test
	public void aferRequestScriptIsCalled() throws Exception
	{
		RestMockResult mockResult = ( RestMockResult )restMockDispatcher.dispatchRequest( request, response );

		verify( restMockService ).runAfterRequestScript( context, mockResult );
	}

	@Test
	public void onRequestScriptIsCalled() throws Exception
	{
		createRestMockDispatcher();
		RestMockResult mockResult = ( RestMockResult )restMockDispatcher.dispatchRequest( request, response );

		verify( restMockService ).runOnRequestScript( any( WsdlMockRunContext.class ), any( MockRequest.class ) );
	}

	@Test
	public void onRequestScriptOverridesRegularDispatching() throws Exception
	{
		/*
			When onRequestScript returns a MockResult instance then regular dispatching is ignored.
			This tests verify when script returns MokResult instance we bypass regular dispatching.

		 */

		createRestMockDispatcher();
		RestMockResult  restMockResult = mock(RestMockResult.class );
		when( restMockService.runOnRequestScript( any( WsdlMockRunContext.class ), any( MockRequest.class ))).thenReturn( restMockResult );

		RestMockResult mockResult = ( RestMockResult )restMockDispatcher.dispatchRequest( request, response );

		// we would like to verify that dispatchRequest is never called but it is hard so we verify on this instead
		verify( restMockService, never() ).findOperationMatchingPath( anyString() );
	}

	private void createRestMockDispatcher()
	{
		request = mock( HttpServletRequest.class );
		Enumeration enumeration = mock( Enumeration.class );
		when( request.getHeaderNames() ).thenReturn( enumeration );

		response = mock( HttpServletResponse.class );
		restMockService = mock( RestMockService.class );
		context = mock( WsdlMockRunContext.class );

		restMockDispatcher = new RestMockDispatcher( restMockService, context );
	}
}
