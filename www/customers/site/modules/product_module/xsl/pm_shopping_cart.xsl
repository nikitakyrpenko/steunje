<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Product Module
  
  Shopping cart and Checkout.
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="customers/site/modules/product_module/xsl/payment/ogone.xsl"/>
<xsl:include href="customers/site/modules/product_module/xsl/payment/ideal.xsl"/>
<!-- ===============SHOPPING CART TEMPLATES START=========== -->
<xsl:template name="pm_carting">
	<script type="text/javascript">
		var lang = '<xsl:value-of select="$lang" />' ;
	</script>
				<!-- HEAD of the shopping cart -->
				<xsl:call-template name="pm_title_line">
					<xsl:with-param name="page">
						<xsl:choose>
							<!-- SHOPPING CART MATCHER -->
							<xsl:when test="@view-type='shopping-cart'">
								<xsl:text>cart</xsl:text>
							</xsl:when>
							<!-- CUSTOMER ACCOUNT MATCHER -->
							<xsl:when test="@view-type='customer-account'">
								<xsl:text>account</xsl:text>
							</xsl:when>
							<!-- CUSTOMER LOGIN MATCHER -->
							<xsl:when test="@view-type='customer-login'">
								<xsl:text>login</xsl:text>
							</xsl:when>
							<!-- CUSTOMER REGISTER MATCHER -->
							<xsl:when test="@view-type='customer-register'">
								<xsl:text>register</xsl:text>
							</xsl:when>
							<!-- CUSTOMER CHECKOUT MATCHER -->
							<xsl:when test="@view-type='checkout-wizard'">
								<xsl:text>checkout_wizard</xsl:text>
							</xsl:when>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>

				<!-- BODY of the shopping cart -->
						<!-- If there are no errors then this template will not be called. So no check is needed. -->
						<xsl:apply-templates select="negeso:errors"/>
						<form method="post" action="" name="wizard_form" id="wizard_form">
							<xsl:choose>
								<!-- SHOPPING CART MATCHER -->
								<xsl:when test="@view-type='shopping-cart'">
                <input type="hidden" name="action" value="update"/>
									<xsl:apply-templates select="negeso:shopping-cart" mode="cart_table"/>
									<xsl:call-template name="pm_shop_product_navbar"/>

								</xsl:when>
								<!-- CUSTOMER ACCOUNT MATCHER -->
								<xsl:when test="@view-type='customer-account'">
									<xsl:attribute name="onSubmit">return onAccountUpdate(this)</xsl:attribute>
									<input type="hidden" name="action" value="update_account"/>
									<input type="hidden" name="customer_password" value=""/>
									<xsl:apply-templates select="negeso:customer-account"/>
								</xsl:when>

								<!-- CUSTOMER LOGIN MATCHER -->
								<xsl:when test="@view-type='customer-login'">
									<xsl:attribute name="onSubmit">upassword.value=hex_md5(p.value); p.value='';</xsl:attribute>
									<xsl:if test="not(negeso:customer-login/@result = 'success')">
										<input type="hidden" name="action" value="login" />
										<input type="hidden" name="upassword" value="" />
									</xsl:if>
									<xsl:apply-templates select="negeso:customer-login" />
								</xsl:when>

								<!-- CUSTOMER REGISTER MATCHER -->
								<xsl:when test="@view-type='customer-register'">
									<xsl:attribute name="onSubmit">return onCustomerRegisterSubmit(this)</xsl:attribute>
									<xsl:if test="not(negeso:customer-register/@result = 'registered') and not(negeso:customer-register/@result = 'activated')">
										<input type="hidden" name="action" value="register"/>
										<input type="hidden" name="customer_password" value=""/>
									</xsl:if>
									<xsl:apply-templates select="negeso:customer-register"/>
								</xsl:when>

								<!-- CUSTOMER CHECKOUT MATCHER -->
								<xsl:when test="@view-type='checkout-wizard'">
									<input type="hidden" name="wizard_action" value="next"/>
									<xsl:apply-templates select="negeso:wizard" mode="pm"/>
								</xsl:when>
							</xsl:choose>
						</form>
</xsl:template>

<xsl:template name="pm_title_line_block">
	<xsl:param name="block_name" />
	<!-- align could be 'left' or 'right' -->
	<xsl:param name="align">right</xsl:param>
	<div style="float: {$align}">
					<xsl:choose>
						<xsl:when test="$block_name='cart_title'">
							<strong>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_SHOPPING_CART'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_SHOPPING_CART')"/>&#160;
							</strong>
							<img src="/site/modules/product_module/images/pm_shop.gif" border="0" align="absMiddle">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_SHOPPING_CART'"/>
								</xsl:call-template>
                    <xsl:attribute name="alt">
                        <xsl:value-of select="java:getString($dict_pm_module, 'PM_SHOPPING_CART')"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="java:getString($dict_pm_module, 'PM_SHOPPING_CART')"/>
                    </xsl:attribute>
							</img>
						</xsl:when>
						<xsl:when test="$block_name='account_title'">
							<strong>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_CUSTOMER_ACCOUNT'"/>
								</xsl:call-template>
                    <xsl:value-of select="java:getString($dict_pm_module, 'PM_CUSTOMER_ACCOUNT')"/>
                </strong>
						</xsl:when>
						<xsl:when test="$block_name='login_title'">
							<strong>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
								</xsl:call-template>
                    <xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>
                </strong>
						</xsl:when>
						<xsl:when test="$block_name='checkout_wizard_title'">
							<!-- It is empty because nothing is needed here in wizard. But it is here to have standard XSL -->
							<strong>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_CHECKOUT_WIZARD'"/>
								</xsl:call-template>
                    <xsl:value-of select="java:getString($dict_pm_module, 'PM_CHECKOUT_WIZARD')"/>
                </strong>
						</xsl:when>
						<xsl:when test="$block_name='register_title'">
							<strong>
								<xsl:choose>
									<xsl:when test="@result = 'registered'">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_pm_module"/>
											<xsl:with-param name ="name"  select="'PM_REGISTRATION_CONFIRMED'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_pm_module, 'PM_REGISTRATION_CONFIRMED')"/>
									</xsl:when>
									<xsl:when test="@result = 'activated'">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_pm_module"/>
											<xsl:with-param name ="name"  select="'PM_REGISTRATION_SUCCESSFUL'"/>
										</xsl:call-template>
			                              	<xsl:value-of select="java:getString($dict_pm_module, 'PM_REGISTRATION_SUCCESSFUL')"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_pm_module"/>
											<xsl:with-param name ="name"  select="'PM_REGISTER'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_pm_module, 'PM_REGISTER')"/>
									</xsl:otherwise>
								</xsl:choose>
							</strong>
						</xsl:when>
						<xsl:when test="$block_name='user_login'">
							<strong>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
								</xsl:call-template>
                    <xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>:
                </strong>&#160;<xsl:value-of select="negeso:login-info/@login"/>&#160;
						</xsl:when>
						<xsl:when test="$block_name='user_name'">
							<strong>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_USER'"/>
								</xsl:call-template>
                    <xsl:value-of select="java:getString($dict_pm_module, 'PM_USER')"/>:
                </strong>&#160;<xsl:value-of select="negeso:login-info/@name"/>&#160;
						</xsl:when>
						<xsl:when test="$block_name='brief_wizard_pages'">
							<xsl:if test="@is-current='true'">
								<xsl:attribute name="class">bold</xsl:attribute>
							</xsl:if>
							<span>
								<xsl:choose>
									<xsl:when test="@id='DELIVERY_METHOD'">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_pm_module"/>
											<xsl:with-param name ="name"  select="'PM_STEP'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_pm_module, 'PM_STEP')"/> 1 ><br/><xsl:value-of select="java:getString($dict_pm_module, 'PM_SHIPPING_METHOD')"/>
								</xsl:when>
								<xsl:when test="@id='SHIP_TO'">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_STEP'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_STEP')"/> 2 ><br/><xsl:value-of select="java:getString($dict_pm_module, 'PM_SHIP_TO')"/>
								</xsl:when>
								<xsl:when test="@id='BILL_TO'">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_STEP'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_STEP')"/> 3 ><br/><xsl:value-of select="java:getString($dict_pm_module, 'PM_BILL_TO')"/>
								</xsl:when>
								<xsl:when test="@id='PREVIEW'">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_STEP'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_STEP')"/> 4 ><br/><xsl:value-of select="java:getString($dict_pm_module, 'PM_PREVIEW')"/>
								</xsl:when>
								<xsl:when test="@id='KIND_OF_PAYMENT'">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_KIND_OF_PAYMENT'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_KIND_OF_PAYMENT')"/>
								</xsl:when>
								<xsl:when test="@id='REDIRECT_TO_ISSUER'">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_REDIRECT_TO_ISSUER'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_REDIRECT_TO_ISSUER')"/>
								</xsl:when>
							</xsl:choose>
							</span>
						</xsl:when>
						<xsl:when test="$block_name='shopping_cart_link'">
							<a class="pmShoppingCartRedLink">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_SHOPPING_CART'"/>
								</xsl:call-template>
								<xsl:call-template name="pm_shopping_cart_link" />
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_SHOPPING_CART')"/>
							</a>&#160;
						</xsl:when>
						<xsl:when test="$block_name='my_profile'">
							<a class="pmShoppingCartRedLink">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_MY_DATA'"/>
								</xsl:call-template>
								<xsl:call-template name="pm_account_link" />
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_MY_DATA')"/>
							</a>&#160;
						</xsl:when>
						<xsl:when test="$block_name='register_link'">
							<a class="pmShoppingCartRedLink">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_REGISTER'"/>
								</xsl:call-template>
								<xsl:call-template name="pm_register_link" />
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_REGISTER')"/>
							</a>&#160;
						</xsl:when>
						<xsl:when test="$block_name='login_link'">
							<a class="pmShoppingCartRedLink">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
								</xsl:call-template>
								<xsl:call-template name="pm_login_link" />
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>
							</a>&#160;
						</xsl:when>
						<xsl:when test="$block_name='logout_link'">
							<a class="pmShoppingCartRedLink">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_LOGOUT'"/>
								</xsl:call-template>
								<xsl:call-template name="pm_login_link" >
									<xsl:with-param name="addition" select="'logout'" />
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGOUT')"/>
							</a>&#160;
						</xsl:when>
					</xsl:choose>
	</div>
</xsl:template>

<xsl:template name="pm_title_line">
    <xsl:param name="page" select='"cart"' />
    <div class="b-pmShoppingCartHead">
		<xsl:call-template name="pm_title_line_block">
			<xsl:with-param name="block_name" select="concat($page,'_title')" />
			<xsl:with-param name="align" select="'left'" />
		</xsl:call-template>

		<!-- ==================== Checkout title: BEGIN ==================== -->

		<!-- Logout link -->
		<!-- It is present at customer account page, on any other page if user is logged in and also on account activation page if activation is successful -->
		<xsl:if test="negeso:login-info/@is-logged='true'">
			<xsl:call-template name="pm_title_line_block">
				<xsl:with-param name="block_name" select="'logout_link'" />
			</xsl:call-template>
		</xsl:if>

		<!-- Login link -->
		<!-- It is present on all pages if user is not logged in and also on account activation page if activation was not done-->
		<xsl:if test="(negeso:login-info/@is-logged='false') and not($page = 'login')">
			<xsl:call-template name="pm_title_line_block">
				<xsl:with-param name="block_name" select="'login_link'" />
			</xsl:call-template>
		</xsl:if>

		<!-- Register link -->
		<!-- It is present only at login page -->
		<xsl:if test="$page = 'login'">
			<xsl:call-template name="pm_title_line_block">
				<xsl:with-param name="block_name" select="'register_link'" />
			</xsl:call-template>
		</xsl:if>

		<!-- Account link -->
		<!-- It is present at any page if user is logged in and on account activation page if activation was done -->
		<xsl:if test="(negeso:login-info/@is-logged='true') and ($page!='account')">
			<xsl:call-template name="pm_title_line_block">
				<xsl:with-param name="block_name" select="'my_profile'" />
			</xsl:call-template>
		</xsl:if>

		<!-- Shopping cart link -->
		<!-- It is present at Account page and Login page and also on account activation page if activation was not done-->
		<xsl:if test="(negeso:login-info/@is-logged='true') and ($page='account' or $page='login' or ($page='register' and @result!='activated'))">
			<xsl:call-template name="pm_title_line_block">
				<xsl:with-param name="block_name" select="'shopping_cart_link'" />
			</xsl:call-template>
		</xsl:if>

		<!-- Output of Checkout stages. I thinks that any checks are not needed because for-each is the same as check -->
		<xsl:for-each select="negeso:wizard/negeso:brief-wizard-pages/negeso:brief-wizard-page">
			<xsl:sort select="position()" order="descending" data-type="number" />
			<xsl:call-template name="pm_title_line_block">
				<xsl:with-param name="block_name" select="'brief_wizard_pages'" />
			</xsl:call-template>
		</xsl:for-each>

		<!-- ==================== Checkout title: END ==================== -->

		<!-- Output of current login and role in case if they are present in xml -->
		<xsl:if test="(count(negeso:login-info/@name) > 0) and not($page = 'checkout_wizard')">
			<xsl:call-template name="pm_title_line_block">
				<xsl:with-param name="block_name" select="'user_name'" />
			</xsl:call-template>
		</xsl:if>

		<xsl:if test="(count(negeso:login-info/@login) > 0) and not($page = 'checkout_wizard')">
			<xsl:call-template name="pm_title_line_block">
				<xsl:with-param name="block_name" select="'user_login'" />
			</xsl:call-template>
		</xsl:if>

	</div>
</xsl:template>


<xsl:template match="negeso:errors">
		<xsl:for-each select="negeso:error">
			<div class="bold" style="color:red">
				<xsl:choose>
					<xsl:when test="text()='Login allreay exists'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_LOGIN_EXIST'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN_EXIST')"/>
					</xsl:when>
					<xsl:when test="text()='Login reqired'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_LOGIN_REQUIRED'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN_REQUIRED')"/>
					</xsl:when>
					<xsl:when test="text()='Login failed'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_LOGIN_FAILED'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN_FAILED')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="text()"/>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:for-each>
</xsl:template>


<xsl:template match="negeso:shopping-cart" mode="cart_table">
    <xsl:param name="can_update" select="'yes'"/>
	<table class="pmShoppingCartTable" cellpadding="2" cellspacing="2">
		<xsl:if test="$can_update='yes'">
			<col width="60" />
		</xsl:if>
		<col width="100" />
		<col width="*" />
		<col width="70" />
		<col width="100" />
		<col>
			<xsl:attribute name="width">
				<xsl:if test="$can_update='no'">
					100
				</xsl:if>
				<xsl:if test="$can_update='yes'">
					120
				</xsl:if>
			</xsl:attribute>
		</col>
		<tr>
			<xsl:if test="$can_update='yes'">
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_DELETE'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_DELETE')"/>
				</th>
			</xsl:if>
			<th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_SERIAL_NUMBER'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_SERIAL_NUMBER')"/>
			</th>
			<th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_PRODUCT_TITLE'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_PRODUCT_TITLE')"/>
			</th>
			<th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_AMOUNT'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_AMOUNT')"/>
			</th>
			<th>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_PRICE'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_PRICE')"/>
			</th>

			<th>
				<xsl:if test="$can_update='no'">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_TOTAL_TAX'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_TOTAL_TAX')"/>
				</xsl:if>
				<xsl:if test="$can_update='yes'">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_TOTAL'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_TOTAL')"/>
				</xsl:if>
			</th>

		</tr>
		<xsl:for-each select="negeso:pm-product">
			<tr>
				<xsl:if test="$can_update='yes'">
					<td align="center">
						<input type="hidden" name="product_ids" value="{@id}"/>
						<input type="checkbox" class="form_checkbox" name="remove_id_{@id}"/>
					</td>
				</xsl:if>
				<td>
					<xsl:value-of select="@sn"/>&#160;
				</td>
				<td>
					<xsl:value-of select="@title"/>&#160;
				</td>
				<td>
					<xsl:choose>
						<xsl:when test="//negeso:pm/@view-type='shopping-cart'">
							<input type="text" class="pmShoppingCartAmount" name="product_amounts" value="{@amount}"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@amount"/>&#160;
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td>
					<xsl:value-of select="@formatted-price"/>&#160;
				</td>
				<xsl:if test="$can_update='no'">
					<td>
						<xsl:value-of select="@total-price"/>&#160;
					</td>
				</xsl:if>

				<xsl:if test="$can_update='yes'">
					<td>
						<xsl:value-of select="@subtotal-price"/>&#160;
					</td>
				</xsl:if>
			</tr>
		</xsl:for-each>

		<xsl:if test="count(negeso:pm-product) != 0">
			<tr>
				<xsl:if test="$can_update='yes'">
					<td colspan="5" class="pmShoppingCartSum right">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_SUBTOTAL'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_SUBTOTAL')"/>:
					</td>
				</xsl:if>
				<xsl:if test="$can_update='no'">
					<td colspan="4" class="pmShoppingCartSum right">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_SUBTOTAL'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_SUBTOTAL')"/>:
					</td>
				</xsl:if>
				<td class="pmShoppingCartSum left">
					<xsl:value-of select="@subtotal-price"/>&#160;
				</td>
			</tr>
			<xsl:if test="$can_update='no' and @delivery-price">
				<tr>
					<td colspan="4" class="pmShoppingCartSum right">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_DELIVERY_PRICE'"/>
						</xsl:call-template>
					    <xsl:value-of select="java:getString($dict_pm_module, 'PM_DELIVERY_PRICE')"/>
					</td>
					<td class="pmShoppingCartSum left">
						<xsl:value-of select="@delivery-price"/>&#160;
					</td>
				</tr>
			</xsl:if>

			<xsl:if test="$can_update='no'">
				<tr>
					<td class="pmShoppingCartSum right bold" colspan="4">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_TOTAL'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_TOTAL')"/>:
					</td>
					<td class="pmShoppingCartSum left">
						<xsl:value-of select="@total-price"/>&#160;
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
    </table>
	<xsl:if test="$can_update='yes'">
		<button type="submit" class="submit">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_UPDATE'"/>
			</xsl:call-template>
			<xsl:if test="count(negeso:pm-product)=0">
				<xsl:attribute name="disabled">true</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_UPDATE')"/>
		</button>
    </xsl:if>
</xsl:template>

<xsl:template match="negeso:customer-account">
        <!-- This is page with information about customer -->
		<xsl:apply-templates select="negeso:login-info"/>
		<xsl:apply-templates select="negeso:shipping-contact"/>
		<xsl:apply-templates select="negeso:billing-contact"/>

		<div>
			<button class="submit" type="submit">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_UPDATE'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_UPDATE')"/>
			</button>
		</div>

		<!-- Navigation bar with links to product module and shopping cart -->
		<xsl:call-template name="pm_shop_product_navbar"/>
</xsl:template>

<xsl:template match="negeso:login-info">
    <fieldset class="b-pmShoppingCartBlockFieldset">
		<legend>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>
        </legend>
		<table cellpadding="0" cellspacing="0" border="0" class="pmShoppingCartCheckoutContact">
			<tr>
				<td class="pmShoppingCartFirstColumn">
					&#160;&#160;
					<span>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_NAME'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_NAME')"/>
					</span>
				</td>
				<td>
					<input type="text" name="customer_name" value="{@name}"/>
				</td>
			</tr>
			<tr>
                <td class="pmShoppingCartFirstColumn">
                    &#160;&#160;
					<span>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_EMAIL_ADRES'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_EMAIL_ADRES')"/>
					</span>
                    *
                </td>
				<td>
					<input type="text" name="customer_email" value="{@email}"/>
				</td>
			</tr>
			<tr>
                <td class="pmShoppingCartFirstColumn">
                    &#160;&#160;
					<span>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_PASSWORD'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_PASSWORD')"/>
                    </span>
                    *
                </td>
				<td>
					<input type="password" name="p" value=""/>
				</td>
			</tr>
			<tr>
                <td class="pmShoppingCartFirstColumn">
                    &#160;&#160;
					<span>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_RETYPE_PASSWORD'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_RETYPE_PASSWORD')"/>
					</span>
                    *
                </td>
				<td>
					<input type="password" name="pret" value=""/>
				</td>
			</tr>
		</table>
	</fieldset>
</xsl:template>

<xsl:template match="negeso:shipping-contact">
    <fieldset class="b-pmShoppingCartBlockFieldset">
		<legend>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_RETYPE_PASSWORD'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_SHIPPING_ADDRESS')"/>
		</legend>
		<xsl:apply-templates select="negeso:contact" mode="pm">
			<xsl:with-param name="prefix" select='"shi"'/>
		</xsl:apply-templates>
	</fieldset>
</xsl:template>

<xsl:template match="negeso:contact" mode="pm">
    <xsl:param name="prefix" />
	<script type="text/javascript">
		function reset1() {
			for (i=1;i&lt;=9;i++)
				document.getElementById("inp000"+i).value="";
		}
	</script>
	<table cellpadding="0" cellspacing="0" border="0" class="pmShoppingCartCheckoutContact">
		<tr>
			<td class="pmShoppingCartFirstColumn">
				&#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_RETYPE_PASSWORD'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_FIRST_NAME')"/>
				</span>*
			</td>
			<td>
				<input id="inp0001" type="text" name="{$prefix}_fname" value="{@first-name}" required="true" />
			</td>
		</tr>
		<tr>
			<td class="pmShoppingCartFirstColumn">
				&#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_LAST_NAME'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_LAST_NAME')"/>
				</span>*
			</td>
			<td>
				<input id="inp0002" type="text" name="{$prefix}_sname" value="{@second-name}" required="true" />
			</td>
		</tr>
		<tr>
			<td class="pmShoppingCartFirstColumn">
				&#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_COMPANY_NAME'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_COMPANY_NAME')"/>
                </span>
				*
			</td>
			<td>
				<input id="inp0003" type="text" name="{$prefix}_company" value="{@company-name}" required="true"/>
			</td>
		</tr>
		<tr>
			<td class="pmShoppingCartFirstColumn">
				&#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_ADDRESS'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_ADDRESS')"/>
				</span>
					*
			</td>
			<td>
				<input id="inp0004" type="text" name="{$prefix}_address" value="{@address-line}" required="true" />
			</td>
		</tr>
		<tr>
			<td class="pmShoppingCartFirstColumn">
				&#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_ZIP_POSTCODE'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_ZIP_POSTCODE')"/>
				</span>
				*
			</td>
			<td>
				<input id="inp0005" type="text" name="{$prefix}_zip" value="{@zip-code}" required="true" />
			</td>
		</tr>
		<tr>
            <td class="pmShoppingCartFirstColumn">
                &#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_CITY'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_CITY')"/>
                </span>
                *
            </td>
			<td >
				<input id="inp0006" type="text" name="{$prefix}_city" value="{@city}" required="true" />
			</td>
		</tr>
		<tr>
            <td class="pmShoppingCartFirstColumn">
                &#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_COUNTRY'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_COUNTRY')"/>
				</span>	*
			</td>
			<td >
				<input id="inp0007" type="text" name="{$prefix}_country" value="{@country}" required="true"/>
			</td>
		</tr>
		<tr>
            <td class="pmShoppingCartFirstColumn">
                &#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_PHONE'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_PHONE')"/>
				</span>*
				</td>
			<td >
				<input id="inp0008" type="text" name="{$prefix}_phone" value="{@phone}" required="true" is_phone="true"/>
			</td>
		</tr>
		<tr>
            <td class="pmShoppingCartFirstColumn">
                &#160;&#160;
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_FAX'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_FAX')"/>
				</span>
			</td>
			<td >
				<input id="inp0009" type="text" name="{$prefix}_fax" value="{@fax}"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="right">
				<input type="button" class="submit" onclick="reset1()">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_RESET_CONTACT_FIELDS'"/>
					</xsl:call-template>
                    <xsl:attribute name="value">
                        <xsl:value-of select="java:getString($dict_pm_module, 'PM_RESET_CONTACT_FIELDS')"/>
                    </xsl:attribute>
				</input>
			</td>
		</tr>
	</table>

</xsl:template>

<xsl:template match="negeso:billing-contact">
	<span>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_pm_module"/>
			<xsl:with-param name ="name"  select="'PM_BILLING_EQUAL_SHIPPING'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_pm_module, 'PM_BILLING_EQUAL_SHIPPING')"/>
    </span>:
	<input type="checkbox" class="pmShoppingCartAmount form_checkbox" name="same_as_shipping" value="" onClick="disableDiv(this)">
		<xsl:if test="@use-shipping-contact = 'true'" >
            <xsl:attribute name="checked">true</xsl:attribute>
		</xsl:if>
	</input>
	<br/>
	<div id="billing_address_div">
		<xsl:if test="@use-shipping-contact = 'true'" >
            <xsl:attribute name="style">display: none</xsl:attribute>
		</xsl:if>
		<br/>
		<fieldset class="pmShoppingCartBlockFieldset">
			<legend>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_BILLING_ADDRESS'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_BILLING_ADDRESS')"/>
			</legend>
			<xsl:apply-templates select="negeso:contact" mode="pm">
				<xsl:with-param name="prefix" select='"bi"'/>
			</xsl:apply-templates>
		</fieldset>
	</div>
	<br/>
</xsl:template>

<xsl:template name="pm_shop_product_navbar">
    <div class="b-pmShoppingCartStep">
        <div class="b-pmShoppingCartDet bl-left">
            <a onfocus="blur()">
					<xsl:attribute name="href">
						<xsl:call-template name="pm_module_link" />
					</xsl:attribute>
					<img src="/site/modules/product_module/images/pm_left.gif" border="0" style="vertical-align: middle;" />
				</a>
				&#160;
				<a>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_CONTINUE_SHOPPING'"/>
					</xsl:call-template>
					<xsl:attribute name="href">
						<xsl:call-template name="pm_module_link" />
					</xsl:attribute>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_CONTINUE_SHOPPING')"/>
				</a>
			</div>
        <div class="b-pmShoppingCartDet bl-right right">
				<xsl:if
						test="count(//negeso:shopping-cart/negeso:pm-product)">
					<a>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_BEGIN_CHECKOUT'"/>
						</xsl:call-template>
						<xsl:call-template name="pm_checkout_link" />
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_BEGIN_CHECKOUT')"/>
					</a>
					&#160;
					<a>
						<xsl:call-template name="pm_checkout_link" />
                    <img src="/site/modules/product_module/images/pm_right.gif" border="0" style="vertical-align: middle;"/>
					</a>
            </xsl:if>
			</div>
		</div>
</xsl:template>

<xsl:template match="negeso:customer-login">
		<xsl:choose>
			<xsl:when test="@result = 'success'">
				<fieldset class="pmShoppingCartBlockFieldset">
					<legend>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>
                </legend>
					<div>
						<br/>
						&#160;&#160;
						<span>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_pm_module"/>
								<xsl:with-param name ="name"  select="'PM_WELCOME'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_WELCOME')"/>
                    </span>,
						<xsl:choose>
                        <xsl:when test="@name">
								<xsl:value-of select="@name"/>
							</xsl:when>
                        <xsl:when test="@login">
								<xsl:value-of select="@login"/>
							</xsl:when>
                    </xsl:choose>,
						<span>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_pm_module"/>
								<xsl:with-param name ="name"  select="'PM_LOGIN_SUCCESS'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN_SUCCESS')"/>
						</span>.
						<br/><br/>
					</div>
				</fieldset>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="negeso:errors"/>
				<fieldset class="pmShoppingCartBlockFieldset">
					<legend>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>
                </legend>
					<table class="pmShoppingCartCheckoutContact" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td class="pmShoppingCartFirstColumn">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>
							</td>
							<td>
								<input type="text" class="pmShoppingCartAmount" name="ulogin" value="{@login}"/>
							</td>
							<td>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_NUMBERS_AND_LETTERS'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_NUMBERS_AND_LETTERS')"/>
							</td>
						</tr>
						<tr>
							<td class="pmShoppingCartFirstColumn">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_PASSWORD'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_PASSWORD')"/>
							</td>
							<td colspan="2">
								<input type="password" class="pmShoppingCartAmount" name="p" value=""/>
							</td>
						</tr>
						<tr>
							<td colspan="3" class="pmShoppingCartFirstUse">
								<xsl:call-template name="pm_first_use" />
							</td>
						</tr>
						<tr>
							<td colspan="3">
								<button class="submit" type="submit">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>
								</button>
							</td>
						</tr>
					</table>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>


<xsl:template match="negeso:customer-register">
		<xsl:apply-templates select="negeso:errors"/>
		<xsl:choose>
			<xsl:when test="@result = 'registered'">
				<fieldset class="pmShoppingCartBlockFieldset">
					<legend>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_ACTIVATION'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_ACTIVATION')"/>
                </legend>
					<table width="100%" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td>
								<br/>
								<span>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_ACTIVATION_LINK'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_ACTIVATION_LINK')"/>
								</span>
                            <br/>
                            <br/>
							</td>
						</tr>
					</table>
            </fieldset>
			</xsl:when>
			<xsl:when test="@result = 'activated'">
				<fieldset class="pmShoppingCartBlockFieldset">
					<legend>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_ACTIVATION'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_ACTIVATION')"/>
                </legend>
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td>
								<br/>
								<a class="red">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_GO_SHOPPING_CART'"/>
									</xsl:call-template>
									<xsl:call-template name="pm_shopping_cart_link" />
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_GO_SHOPPING_CART')"/>
								</a>
                            <br/>
                            <br/>
							</td>
						</tr>
					</table>
				</fieldset>
			</xsl:when>
			<xsl:otherwise>
				<fieldset class="pmShoppingCartBlockFieldset">
					<legend>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_REGISTER'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_REGISTER')"/>
                </legend>

					<table cellpadding="0" cellspacing="0" border="0" class="pmShoppingCartCheckoutContact">
						<tr>
							<td class="pmShoppingCartFirstColumn">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_LOGIN'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_LOGIN')"/>*
							</td>
							<td>
								<input type="text" name="customer_login" value="{@login}"/>&#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_NUMBERS_AND_LETTERS')"/>
							</td>
						</tr>
						<tr>
							<td class="pmShoppingCartFirstColumn">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_NAME'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_NAME')"/>
							</td>
							<td>
								<input type="text" name="customer_name" value="{@name}"/>
							</td>
						</tr>
						<tr>
							<td class="pmShoppingCartFirstColumn">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_EMAIL_ADRES'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_EMAIL_ADRES')"/>*
							</td>
							<td>
								<input type="text" name="customer_email" value="{@email}"/>
							</td>
						</tr>
						<tr>
							<td class="pmShoppingCartFirstColumn">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_PASSWORD'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_PASSWORD')"/>*
							</td>
							<td>
								<input type="password" name="p" value=""/>
							</td>
						</tr>
						<tr>
							<td class="pmShoppingCartFirstColumn">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_RETYPE_PASSWORD'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_RETYPE_PASSWORD')"/>*
							</td>
							<td>
								<input type="password" name="pret" value=""/>
							</td>
						</tr>
						<tr>
							<td colspan="2">
                            <div class="red">
                                <p>
                                    &#160;*
								<span>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_REQUIRED_FIELDS'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_pm_module, 'PM_REQUIRED_FIELDS')"/>
                                    </span>
                                </p>
							</div>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input class="submit" type="submit">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_REGISTER'"/>
									</xsl:call-template>
                                <xsl:attribute name="value">
                                    <xsl:value-of select="java:getString($dict_pm_module, 'PM_REGISTER')"/> &#062;&#062;&#062;
                                </xsl:attribute>
								</input>
							</td>
						</tr>
					</table>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>


<xsl:template match="negeso:wizard" mode="pm">
	<xsl:apply-templates select="negeso:wizard-page"/>
	<xsl:if test="not(negeso:wizard-page[@id='RESULT'])">
		<div class="pmShoppingCartSubmit">
			<input type="button" class="submit">
                           <xsl:choose>
                            <xsl:when test="negeso:brief-wizard-pages[negeso:brief-wizard-page[@step='1'][@is-current='true']]">
                                <xsl:attribute name="onClick">
                                    <xsl:text>javascript:window.location.href = '</xsl:text>
                                    <xsl:value-of select="$adminPath"/>
                                    <xsl:text>/shopping_cart_</xsl:text>
                                    <xsl:value-of select="$lang"/>
                                    <xsl:text>.html'</xsl:text>
                                 </xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="onClick">doPrevious(this)</xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_BACK'"/>
				</xsl:call-template>
				<xsl:choose>
					<xsl:when test="negeso:wizard-page/@has-previous = 'true'">
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="disabled">true</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:attribute name="value">
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_BACK')"/>
				</xsl:attribute>
            </input>
			&#160;
			<xsl:if test="negeso:wizard-page/@has-next = 'true'">
				<input type="button" class="submit" onclick="if (validate(this.form)) doNext(this);">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_NEXT'"/>
					</xsl:call-template>
					<xsl:choose>
						<xsl:when test="negeso:wizard-page/@has-next = 'true'">
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="disabled">true</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:attribute name="value">
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_NEXT')"/>
					</xsl:attribute>
				</input>
			</xsl:if>
			&#160;
			<xsl:if test="negeso:wizard-page/@can-finish = 'true'">
				<input type="button" align="right" class="submit" id="finish" onclick="if (validate(this.form)) doFinish(this)">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_FINISH'"/>
					</xsl:call-template>
					<xsl:if test="not(negeso:wizard-page/@can-finish = 'true')">
						<xsl:attribute name="disabled">true</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="value">
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_FINISH')"/>
					</xsl:attribute>
				</input>
			</xsl:if>
		</div>
	</xsl:if>
</xsl:template>

<xsl:template match="negeso:wizard-page[@id='DELIVERY_METHOD']">
	<xsl:apply-templates select="negeso:shopping-cart" mode="cart_table">
		<xsl:with-param name="can_update" select='"no"' />
	</xsl:apply-templates>

	<div>
		<xsl:if test="negeso:errors">
			<br/>
			<xsl:apply-templates select="negeso:errors" />
		</xsl:if>
	</div>

	<br />
	<fieldset class="pmShoppingCartBlockFieldset">
		<legend class="pmShoppingCartMenuTop">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_CLIENT'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_CLIENT')"/>
		</legend>
		<xsl:for-each select="negeso:customer">
			<table border="0" width="100%">
				<tr>
					<td width="180px">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_EMAIL_ADRES'"/>
						</xsl:call-template>
						&#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_EMAIL_ADRES')"/>
					</td>
					<td>
						<input type="text" style="width: 300px" name="customer_email" value="{@email}" is_email="true" required="true" />
					</td>
				</tr>
			</table>
		</xsl:for-each>
	</fieldset>
	<br />
	<fieldset class="pmShoppingCartBlockFieldset">
		<legend class="pmShoppingCartMenuTop">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_SHIP_OPTIONS'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_SHIP_OPTIONS')"/>
		</legend>
		<xsl:for-each select="negeso:delivery-methods">
			<xsl:for-each select="negeso:delivery-method">
				<table border="0" width="100%">
					<tr>
						<td width="180">
							&#160;&#160;<xsl:value-of select="@title" />,&#160;<xsl:value-of select="@price" />
						</td>
						<td>
							<input class="pmShoppingCartCheck form_radio" type="radio" name="delivery_method_id" value="{@id}">
								<xsl:if test="@active='true'">
									<xsl:attribute name="CHECKED">
										true
									</xsl:attribute>
								</xsl:if>
								<xsl:if
									test="(@id = 1) and (count(@active) = 0)">
									<xsl:attribute name="CHECKED">
										true
									</xsl:attribute>
								</xsl:if>
							</input>
						</td>
					</tr>
				</table>
			</xsl:for-each>
		</xsl:for-each>
	</fieldset>
</xsl:template>

<xsl:template match="negeso:wizard-page[@id='SHIP_TO']">
	<xsl:apply-templates select="negeso:shopping-cart" mode="cart_table">
		<xsl:with-param name="can_update" select='"no"' />
	</xsl:apply-templates>
	<div class="heading">
		<xsl:apply-templates select="negeso:errors" />
	</div>
	<br/>
	<fieldset class="pmShoppingCartBlockFieldset">
		<legend class="pmShoppingCartMenuTop">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_SHIPPING_ADDRESS'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_SHIPPING_ADDRESS')"/>
		</legend>
		<xsl:apply-templates select="negeso:contact" mode="pm">
			<xsl:with-param name="prefix" select='"shi"' />
		</xsl:apply-templates>
	</fieldset>
</xsl:template>

<xsl:template match="negeso:wizard-page[@id='BILL_TO']">
	<xsl:apply-templates select="negeso:shopping-cart" mode="cart_table">
		<xsl:with-param name="can_update" select='"no"' />
	</xsl:apply-templates>
	<div class="heading">
		<xsl:apply-templates select="negeso:errors" />
	</div>
	<br />
	<fieldset class="pmShoppingCartBlockFieldset">
		<legend class="pmShoppingCartMenuTop">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_BILLING_ADDRESS'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_BILLING_ADDRESS')"/>
		</legend>
		<xsl:apply-templates select="negeso:contact" mode="pm">
			<xsl:with-param name="prefix" select='"bi"' />
		</xsl:apply-templates>
	</fieldset>
</xsl:template>

<xsl:template match="negeso:wizard-page[@id='PREVIEW']">
	<xsl:param name="iDeal">iDeal</xsl:param>

	<xsl:apply-templates select="negeso:shopping-cart" mode="cart_table">
		<xsl:with-param name="can_update" select='"no"' />
	</xsl:apply-templates>

	<div class="heading">
		<xsl:apply-templates select="negeso:errors" />
	</div>

	<xsl:for-each select="negeso:delivery-method">
		<fieldset class="pmShoppingCartBlockFieldset">
			<legend class="pmShoppingCartMenuTop">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_SHIP_OPTIONS'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_SHIP_OPTIONS')"/>
			</legend>
			<table border="0" width="100%">
				<tr>
					<td width="280px">
						&#160;&#160;<xsl:value-of select="@title" />
					</td>
					<td>
						<xsl:value-of select="@price" />
					</td>
				</tr>
			</table>
		</fieldset>
	</xsl:for-each>

	<xsl:for-each select="negeso:customer">
		<fieldset class="pmShoppingCartBlockFieldset">
			<legend class="pmShoppingCartMenuTop">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_CLIENT'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_CLIENT')"/>
			</legend>
			<table border="0" width="100%">
				<tr>
					<td width="280px">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_EMAIL'"/>
						</xsl:call-template>
						&#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_EMAIL')"/>
					</td>
					<td>
						<xsl:value-of select="@email" />
					</td>
				</tr>
			</table>
		</fieldset>
	</xsl:for-each>

	<xsl:for-each select="negeso:shipping-contact">
		<fieldset class="pmShoppingCartBlockFieldset">
			<legend class="pmShoppingCartMenuTop">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_SHIPPING_ADDRESS'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_SHIPPING_ADDRESS')"/>
			</legend>
			<xsl:apply-templates select="negeso:contact" mode="pm_view" />
		</fieldset>
	</xsl:for-each>

	<xsl:if test="not($iDeal='true')">
		<xsl:for-each select="negeso:billing-contact">
			<fieldset class="pmShoppingCartBlockFieldset">
				<legend class="pmShoppingCartMenuTop">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_BILLING_ADDRESS'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_BILLING_ADDRESS')"/>
				</legend>
				<xsl:apply-templates select="negeso:contact" mode="pm_view" />
			</fieldset>
		</xsl:for-each>
	</xsl:if>

	<fieldset class="pmShoppingCartBlockFieldset">
		<legend class="pmShoppingCartMenuTop">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_COMMENT'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_COMMENT')"/>
		</legend>
		<table border="0" width="100%">
			<tr valign="top">
				<td>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_COMMENT'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_COMMENT')"/>
				</td>
				<td>
					<textarea type="text" name="pmOrderComment" class="pmShoppingCartTextarea" data_type="text" rows="4" style="width:80%">
						<xsl:value-of select="@pmOrderComment" />
					</textarea>
				</td>
			</tr>
			<tr>
				<td colspan="2">&#160;</td>
			</tr>
			<tr>
				<td width="280px">
					<label for="agreed">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_CONDITIONS_AGREE_1'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_CONDITIONS_AGREE_1')"/>&#160;
						<a target="_blank" class="pmShoppingCartRedLink">
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_pm_module"/>
								<xsl:with-param name ="name"  select="'PM_CONDITIONS_AGREE_2'"/>
							</xsl:call-template>
							<xsl:call-template name="pm_service_conditions_link" />
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_CONDITIONS_AGREE_2')"/>
						</a>
						:
					</label>
				</td>
				<td>
					<input id="agreed" type="checkbox" class="form_checkbox" name="agreeConditions"/>
                </td>
			</tr>
		</table>
	</fieldset>
</xsl:template>


<xsl:template match="negeso:wizard-page[@id='KIND_OF_PAYMENT']">
	
	<xsl:apply-templates select="negeso:shopping-cart" mode="cart_table">
		<xsl:with-param name="can_update" select='"no"' />
	</xsl:apply-templates>
	<div class="heading">
		<xsl:apply-templates select="negeso:errors" />
	</div>
	<fieldset class="pmShoppingCartBlockFieldset">
		<legend class="pmShoppingCartMenuTop">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_KIND_OF_PAYMENT'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_KIND_OF_PAYMENT')"/>
		</legend>
		<table>
		  <xsl:apply-templates select="negeso:payment-method"/>
		</table>
		<br/>
	</fieldset>
</xsl:template>

<xsl:template match="negeso:payment-method">
    <tr>
        <td>
			<img src="/site/modules/product_module/images/cash.png" alt="" width="30px" height="30px" border="0" hspace="0" vspace="0" align="middle" style="margin: 4px; margin-left: 10px;" >
		        <xsl:if test="not(@logo = '')">
		            <xsl:attribute name="src">
		                <xsl:value-of select="@logo"/>
		            </xsl:attribute>
		        </xsl:if>
		    </img>
	    </td>
	    <td>
			<input type="radio" class="form_radio" name="payment_method_id" onclick="enableChoosingBank()">
				<xsl:attribute name="value">
					<xsl:value-of select="negeso:id" />
				</xsl:attribute>
				<xsl:if test="negeso:current='true'">
					<xsl:attribute name="checked">true</xsl:attribute>
				</xsl:if>
			</input>
		</td>
		<td>
		    <span style="line-height: 20px; vertical-align: middle">
		        &#160;<xsl:value-of select="negeso:name" />
		    </span>
	    </td>
    </tr>
</xsl:template>

<xsl:template match="negeso:contact" mode="pm_view">
    <xsl:param name="prefix" />
	<table class="pmShoppingCartPayment" cellpadding="2" cellspacing="2" border="0">
		<tr>
			<td width="280px">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_FIRST_NAME'"/>
				</xsl:call-template>
                &#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_FIRST_NAME')"/>
            </td>
            <td>
                <xsl:value-of select="@first-name"/>
            </td>
		</tr>
		<tr>
			<td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_LAST_NAME'"/>
				</xsl:call-template>
                &#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_LAST_NAME')"/>
            </td>
            <td>
                <xsl:value-of select="@second-name"/>
            </td>
		</tr>
		<tr>
			<td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_COMPANY_NAME'"/>
				</xsl:call-template>
                &#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_COMPANY_NAME')"/>
            </td>
            <td>
                <xsl:value-of select="@company-name"/>
            </td>
		</tr>
		<tr>
			<td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_ADDRESS'"/>
				</xsl:call-template>
                &#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_ADDRESS')"/>
            </td>
            <td>
                <xsl:value-of select="@address-line"/>
            </td>
		</tr>
		<tr>
			<td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_ZIP_POSTCODE'"/>
				</xsl:call-template>
                &#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_ZIP_POSTCODE')"/>
            </td>
            <td>
                <xsl:value-of select="@zip-code"/>
            </td>
		</tr>
		<tr>
			<td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_CITY'"/>
				</xsl:call-template>
                &#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_CITY')"/>
            </td>
            <td>
                <xsl:value-of select="@city"/>
            </td>
		</tr>
		<tr>
			<td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_COUNTRY'"/>
				</xsl:call-template>
                &#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_COUNTRY')"/>
            </td>
            <td>
                <xsl:value-of select="@country"/>
            </td>
		</tr>
		<tr>
			<td>&#160;&#160;Telefoon</td>
            <td>
                <xsl:value-of select="@phone"/>
            </td>
		</tr>
		<tr>
			<td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_FAX'"/>
				</xsl:call-template>
                &#160;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_FAX')"/>
            </td>
            <td>
                <xsl:value-of select="@fax"/>
            </td>
		</tr>
	</table>
</xsl:template>

<xsl:template match="negeso:wizard-page[@id='RESULT']">
	<table class="pmShoppingCartTable">
        <tr>
            <td>
	<xsl:choose>
		<xsl:when  test="not(negeso:errors)">
			<div>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_ORDER_COMPLETED'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_ORDER_COMPLETED')"/>
			</div>
			<div>
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_YOUR_ORDER_ID'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_YOUR_ORDER_ID')"/>
				</span>
				&#160;<xsl:value-of select="//@order-id"/>
			</div>
			<div>
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_YOUR_CUSTOMER_ID'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_YOUR_CUSTOMER_ID')"/>
				</span>
				&#160;<xsl:value-of select="//@customer-id"/>
			</div>
			<xsl:call-template name="pm_shopping_cart_links"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="negeso:errors"/>
			<xsl:call-template name="pm_shopping_cart_links"/>
		</xsl:otherwise>
	</xsl:choose>
	</td>
	</tr>
	</table>
	<br/>
</xsl:template>

<xsl:template name="pm_shopping_cart_links">
	<div>
		<a class="pmShoppingCartRedLink">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_RETURN_TO_START'"/>
			</xsl:call-template>
			<xsl:attribute name="href">
				<xsl:call-template name="pm_module_link" />
			</xsl:attribute>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_RETURN_TO_START')"/>
		</a>
	</div>
</xsl:template>
<!-- ===============SHOPPING CART TEMPLATES END=========== -->

</xsl:stylesheet>