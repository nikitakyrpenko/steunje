<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2003 Negeso Ukraine
  
  Harvester configuration. Every signed field
  is harvested by form_manager to database.
  I name=dbname use name only.
  WARNING: All dbnames for field must be in table 
  forms_harvester.


  @version      $Revision$
  @author       Olexiy.Strashko
-->

<harvester>
	<field name="name"/>
	<field name="* Voorletters" dbname="voorletters"/>
	<field name="tussenvoegsel"/>
	<field name="geboortedatum"/>
	<field name="adres"/>
	<field name="huisnummer"/>
	<field name="postcode"/>
	<field name="plaats"/>
	<field name="land"/>
	<field name="telefoonnummer"/>
	<field name="mobiel_nummer"/>
	<field name="email"/>
	<field name="subscribe_newsletter"/>
</harvester>
