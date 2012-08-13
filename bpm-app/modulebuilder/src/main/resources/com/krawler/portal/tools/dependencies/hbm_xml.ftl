<#list entities as entity>
	<#if entity.hasColumns()>
		<class name="${packagePath}.${entity.getName()}" table="${entity.getTable()}">

            <#if entity.hasCompoundPK()>
                <composite-id name="primaryKey" class="${packagePath}.service.persistence.${entity.getName()}PK">
					<#assign pkList = entity.getPKList()>

					<#list pkList as column>
						<key-property name="${column.getName()}"

						<#if column.getName() != column.DBName>
							column="${column.DBName}"
						</#if>

						<#if column.isPrimitiveType() || column.type == "String">
							type="com.liferay.portal.dao.orm.hibernate.${serviceBuilder.getPrimitiveObj("${column.type}")}Type"
                        </#if>

						<#if column.type == "Date">
							type="org.hibernate.type.TimestampType"
						</#if>

						/>
					</#list>
				</composite-id>
            <#else>
				<#assign column = entity.getPKList()?first>

				<id name="${column.getName()}"

					type="<#if !entity.hasPrimitivePK()>java.lang.</#if>${column.getType()}">

					<#assign class = "uuid">

					<generator class="${class}"

					<#if class == "sequence">
							><param name="sequence">${column.idParam}</param>
						</generator>
					<#else>
						/>
					</#if>
				</id>
			</#if>

            <#if entity.hasForeignKey()>
                <#list entity.getFKList() as column>
                    <many-to-one cascade="delete"
                        class="com.krawler.esp.hibernate.impl.${column.getType()}"
                        column="${column.getName()}" name="${column.getName()}"/>
                </#list>
            </#if>

			<#list entity.getColumnList() as column>
				<#if !column.isPrimary() && !column.isCollection() && !column.isForeign()>
					<property name="${column.getName()}"

					<#if column.isPrimitiveType() || column.getType() == "String">
						type="${serviceBuilder.getPrimitiveObj("${column.getType()}")}"
                    </#if>

					<#if column.getType() == "Date">
						type="org.hibernate.type.TimestampType"
					</#if>

					/>
				</#if>
			</#list>
		</class>
	</#if>
</#list>