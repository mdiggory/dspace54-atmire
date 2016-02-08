package com.atmire.app.xmlui.aspect.externalhandle;

import com.atmire.lne.content.ItemService;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.util.HashMap;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TODO TOM UNIT TEST
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemViewerTest {

    @InjectMocks
    private ItemViewer itemViewer = new ItemViewer() {
        public void setup(final SourceResolver resolver, final Map objectModel, final String src, final Parameters parameters) throws ProcessingException, SAXException, IOException {
            this.objectModel = objectModel;
            this.parameters = parameters;
            try {
                this.context = ContextUtil.obtainContext(objectModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.eperson = context.getCurrentUser();
            Request request = ObjectModelHelper.getRequest(objectModel);
            this.contextPath = "test.com";
        }
    };

    @Mock
    private ItemService itemService;

    @Mock
    private SourceResolver resolver;

    private Map objectModel;

    @Mock
    private Parameters parameters;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Request request;

    @Mock
    private Body body;

    @Mock
    private Item item;

    @Mock
    private Context context;


    @Before
    public void setUp() throws SAXException, IOException, ProcessingException {
        objectModel = new HashMap();

        objectModel.put(HttpEnvironment.HTTP_RESPONSE_OBJECT, response);
        objectModel.put(ObjectModelHelper.REQUEST_OBJECT, request);

        when(request.getSitemapURI()).thenReturn("external-handle/1337");
        when(item.getHandle()).thenReturn("123456/789");
        when(request.getAttribute(ContextUtil.DSPACE_CONTEXT)).thenReturn(context);


        itemViewer.setup(resolver, objectModel, "", parameters);
    }

    @Test
    public void testAddBody() throws Exception {
        when(itemService.findItemsByExternalHandle(context, "1337")).thenReturn(Arrays.asList(item));

        itemViewer.addBody(body);

        verify(response).sendRedirect("test.com/handle/123456/789");
    }
}