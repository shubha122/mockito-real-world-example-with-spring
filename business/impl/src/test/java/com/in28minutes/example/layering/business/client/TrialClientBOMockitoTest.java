package com.in28minutes.example.layering.business.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.in28minutes.example.layering.business.api.client.ClientBO;
import com.in28minutes.example.layering.business.impl.client.ClientBOImpl;
import com.in28minutes.example.layering.data.api.client.ClientDO;
import com.in28minutes.example.layering.data.api.client.ProductDO;
import com.in28minutes.example.layering.model.api.client.*;
import com.in28minutes.example.layering.model.impl.client.AmountImpl;
import com.in28minutes.example.layering.model.impl.client.ClientImpl;
import com.in28minutes.example.layering.model.impl.client.ProductImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TrialClientBOMockitoTest {
    @Mock
    ProductDO productDO;
    @Mock
    ClientDO clientDO;

    @InjectMocks
   private ClientBO clientBO = new ClientBOImpl();

    @Captor
    ArgumentCaptor<Client> clientArgumentCaptor;

    private static final int DUMMY_CLIENT_ID = 1;
    @Test
    public void TestGetClientProductsSum() throws JsonProcessingException {

        List<Product> existingProducts = Arrays.asList(createProductWithAmount("5.0"),
                createProductWithAmount("6.0"));
        when(productDO.getAllProducts(anyInt())).thenReturn(existingProducts);

       Amount actual= clientBO.getClientProductsSum(DUMMY_CLIENT_ID);
        BigDecimal bml = new BigDecimal("11.0");
       AmountImpl aml = new AmountImpl(bml,Currency.EURO);
       Amount expected =aml;
        assertAmountEquals(actual,aml);

    }
private void assertAmountEquals(Amount expectedamt, Amount actualamt){
        Assert.assertEquals(expectedamt.getValue(),actualamt.getValue());
    Assert.assertEquals(expectedamt.getCurrency(),actualamt.getCurrency());

}
    private Product createProductWithAmount(String amount) {
        BigDecimal bml = new BigDecimal(amount);
        AmountImpl aml = new AmountImpl(bml,Currency.EURO);
        ProductImpl pml = new ProductImpl(100,"Shubha",ProductType.BANK_GUARANTEE,aml);
        return pml;
    }
@Test
    public void TestsaveChangedProducts_update() throws JsonProcessingException {
        Product screenprod = createProductWithAmount("9.0");

        List<Product> dbprods = Arrays.asList(createProductWithAmount("8.0"));

        List<Product> screenprods = Arrays.asList(screenprod);
        stub(productDO.getAllProducts(anyInt())).toReturn(dbprods);

        clientBO.saveChangedProducts(DUMMY_CLIENT_ID,screenprods);
        verify(productDO).updateProduct(DUMMY_CLIENT_ID,screenprod);
    }

    @Test
    public void TestsaveChangedProducts_insert()  {
        List<Product>  screenpods = Arrays.asList(createProduct());
        List<Product> emptydbprods = new ArrayList<>();
        stub(productDO.getAllProducts(anyInt())).toReturn(emptydbprods);
        clientBO.saveChangedProducts(DUMMY_CLIENT_ID,screenpods);
        verify(productDO).insertProduct(DUMMY_CLIENT_ID,screenpods.get(0));
    }

    private Product createProduct() {
        BigDecimal bml = new BigDecimal("3.0");
        AmountImpl aml = new AmountImpl(bml,Currency.EURO);
        ProductImpl pml = new ProductImpl(100,"Shubha",ProductType.BANK_GUARANTEE,aml);
        return pml;
    }
    @Test
    public void TestsaveChangedProducts_delete() {
       Product prodfromdb = createProduct();
        List<Product>  dbprods = Arrays.asList(prodfromdb);
        List<Product> emptyscreenprods = new ArrayList<>();
        stub(productDO.getAllProducts(anyInt())).toReturn(dbprods);
        clientBO.saveChangedProducts(DUMMY_CLIENT_ID,emptyscreenprods);
        verify(productDO).deleteProduct(DUMMY_CLIENT_ID,prodfromdb);
    }
    @Test
    public void TestcalculateAndSaveClientProductSum() {
        ClientImpl client = createClientWithProduct(createProductWithAmount("6.0"), createProductWithAmount("6.0"));
        clientBO.calculateAndSaveClientProductSum(client);
        verify(clientDO).saveClient(clientArgumentCaptor.capture());
        assertEquals(new BigDecimal("12.0"),clientArgumentCaptor.getValue().getProductAmount());

    }

    private ClientImpl createClientWithProduct(Product... products) {
        ClientImpl client = new ClientImpl(0,null,null,null,
                Arrays.asList(products));
        return client;

    }

}