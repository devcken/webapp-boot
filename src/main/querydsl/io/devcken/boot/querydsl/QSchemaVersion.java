package io.devcken.boot.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QSchemaVersion is a Querydsl query type for QSchemaVersion
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QSchemaVersion extends com.querydsl.sql.RelationalPathBase<QSchemaVersion> {

    private static final long serialVersionUID = 1675466579;

    public static final QSchemaVersion schemaVersion = new QSchemaVersion("schema_version");

    public final NumberPath<Integer> checksum = createNumber("checksum", Integer.class);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> executionTime = createNumber("executionTime", Integer.class);

    public final StringPath installedBy = createString("installedBy");

    public final DateTimePath<java.sql.Timestamp> installedOn = createDateTime("installedOn", java.sql.Timestamp.class);

    public final NumberPath<Integer> installedRank = createNumber("installedRank", Integer.class);

    public final StringPath script = createString("script");

    public final BooleanPath success = createBoolean("success");

    public final StringPath type = createString("type");

    public final StringPath version = createString("version");

    public final NumberPath<Integer> versionRank = createNumber("versionRank", Integer.class);

    public final com.querydsl.sql.PrimaryKey<QSchemaVersion> primary = createPrimaryKey(version);

    public QSchemaVersion(String variable) {
        super(QSchemaVersion.class, forVariable(variable), "null", "schema_version");
        addMetadata();
    }

    public QSchemaVersion(String variable, String schema, String table) {
        super(QSchemaVersion.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QSchemaVersion(Path<? extends QSchemaVersion> path) {
        super(path.getType(), path.getMetadata(), "null", "schema_version");
        addMetadata();
    }

    public QSchemaVersion(PathMetadata metadata) {
        super(QSchemaVersion.class, metadata, "null", "schema_version");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(checksum, ColumnMetadata.named("checksum").withIndex(7).ofType(Types.INTEGER).withSize(10));
        addMetadata(description, ColumnMetadata.named("description").withIndex(4).ofType(Types.VARCHAR).withSize(200).notNull());
        addMetadata(executionTime, ColumnMetadata.named("execution_time").withIndex(10).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(installedBy, ColumnMetadata.named("installed_by").withIndex(8).ofType(Types.VARCHAR).withSize(100).notNull());
        addMetadata(installedOn, ColumnMetadata.named("installed_on").withIndex(9).ofType(Types.TIMESTAMP).withSize(19).notNull());
        addMetadata(installedRank, ColumnMetadata.named("installed_rank").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(script, ColumnMetadata.named("script").withIndex(6).ofType(Types.VARCHAR).withSize(1000).notNull());
        addMetadata(success, ColumnMetadata.named("success").withIndex(11).ofType(Types.BIT).notNull());
        addMetadata(type, ColumnMetadata.named("type").withIndex(5).ofType(Types.VARCHAR).withSize(20).notNull());
        addMetadata(version, ColumnMetadata.named("version").withIndex(3).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(versionRank, ColumnMetadata.named("version_rank").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

