---
description: This page describes the Part II of the Labo 2
---

# C1 - RDS

## Create and migrate a database using the Relational Database Service (RDS)

****[**AWS Official Doc**](https://aws.amazon.com/rds/)****

### Prerequisites

* [x] Create an AMI of your Drupal Instance (CLD-INSTANCE-DEVTEAM\[XX])

```
[INPUT]
aws ec2 create-image \
    --instance-id i-04ca2d2bc22e876a3 \
    --name "CLD-AMI-DEVTEAM07" \
    --description "AMI image groupe 7"

[OUTPUT]
ImageId: ami-0b2c1d5fcf5cdc98f
```

* [x] Terminate your instance

```
[INPUT]
aws ec2 terminate-instances --instance-ids i-04ca2d2bc22e876a3
[OUTPUT]
TerminatingInstances:
- InstanceId:i-04ca2d2bc22e876a3
  CurrentState:
    Code: 32
    Name: shutting-down
  PreviousState:
    Code: 16
    Name: running
```

* [x] Delete your private subnet

```
[INPUT]
aws ec2 delete-subnet 
      --subnet-id subnet-0e80be6dcbf923a59
[OUTPUT]
None
```

* [x] Create two new subnets sharing the ip range like this
  * [x] 10.0.07.0/28 -> eu-south-1a -> <mark style="color:red;">CLD-SUBNET-PRIVATE-DEVOPSTEAM\[XX]\_A</mark>
  * [x] 10.0.07.128/28 -> eu-south-1b -> <mark style="color:red;">CLD-SUBNET-PRIVATE-DEVOPSTEAM\[XX]\_B</mark>

```
[INPUT]
aws ec2 create-subnet \
    --availability-zone eu-south-1a \
    --cidr-block 10.0.7.0/28 \
    --vpc-id vpc-0ae930118f4e3cf79 \
    --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=CLD-SUBNET-PRIVATE-DEVOPSTEAM07_A}]'
aws ec2 create-subnet \
    --availability-zone eu-south-1b \
    --cidr-block 10.0.7.128/28 \
    --vpc-id vpc-0ae930118f4e3cf79 \
    --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=CLD-SUBNET-PRIVATE-DEVOPSTEAM07_B}]'
[OUTPUT]
Subnet:
  AssignIpv6AddressOnCreation: false
  AvailabilityZone: eu-south-1a
  AvailabilityZoneId: eus1-az1
  AvailableIpAddressCount: 11
  CidrBlock: 10.0.7.0/28
  DefaultForAz: false
  EnableDns64: false
  Ipv6CidrBlockAssociationSet: []
  Ipv6Native: false
  MapPublicIpOnLaunch: false
  OwnerId: '709024702237'
  PrivateDnsNameOptionsOnLaunch:
    EnableResourceNameDnsAAAARecord: false
    EnableResourceNameDnsARecord: false
    HostnameType: ip-name
  State: available
  SubnetArn: arn:aws:ec2:eu-south-1:709024702237:subnet/subnet-0e80be6dcbf923a59
  SubnetId: subnet-0e80be6dcbf923a59
  Tags:
  - Key: Name
    Value: CLD-SUBNET-PRIVATE-DEVOPSTEAM07_A
  VpcId: vpc-0ae930118f4e3cf79
Subnet:
  AssignIpv6AddressOnCreation: false
  AvailabilityZone: eu-south-1b
  AvailabilityZoneId: eus1-az2
  AvailableIpAddressCount: 11
  CidrBlock: 10.0.7.128/28
  DefaultForAz: false
  EnableDns64: false
  Ipv6CidrBlockAssociationSet: []
  Ipv6Native: false
  MapPublicIpOnLaunch: false
  OwnerId: '709024702237'
  PrivateDnsNameOptionsOnLaunch:
    EnableResourceNameDnsAAAARecord: false
    EnableResourceNameDnsARecord: false
    HostnameType: ip-name
  State: available
  SubnetArn: arn:aws:ec2:eu-south-1:709024702237:subnet/subnet-0e8ffe3df60187d0b
  SubnetId: subnet-0e8ffe3df60187d0b
  Tags:
  - Key: Name
    Value: CLD-SUBNET-PRIVATE-DEVOPSTEAM07_B
  VpcId: vpc-0ae930118f4e3cf79
```

<figure><img src="../../../.gitbook/assets/image (3).png" alt=""><figcaption><p>Result expected</p></figcaption></figure>

### **Step 1: Create DB subnet group**

{% hint style="info" %}
Go to AWS "RDS MANAGEMENT CONSOLE"
{% endhint %}

* Subnet group details

| Variable    | Value                               |
| ----------- | ----------------------------------- |
| Name        | CLD-DB-SUBNET-GROUP-DEVOPSTEAM\[XX] |
| Description | CLD-DB-SUBNET-GROUP-DEVOPSTEAM\[XX] |
| VPC         | CLD-VPC                             |

```
[INPUT]
aws rds create-db-subnet-group \
    --db-subnet-group-name CLD-DBSUBNET-DEVTEAM07 \
    --db-subnet-group-description CLD-DBSUBNET-DEVTEAM07 \
    --subnet-ids '["subnet-0e80be6dcbf923a59", "subnet-0e8ffe3df60187d0b"]'
[OUTPUT]
DBSubnetGroup:
  DBSubnetGroupArn: arn:aws:rds:eu-south-1:709024702237:subgrp:cld-dbsubnet-devteam07
  DBSubnetGroupDescription: CLD-DBSUBNET-DEVTEAM07
  DBSubnetGroupName: cld-dbsubnet-devteam07
  SubnetGroupStatus: Complete
  Subnets:
  - SubnetAvailabilityZone:
      Name: eu-south-1b
:...skipping...
DBSubnetGroup:
  DBSubnetGroupArn: arn:aws:rds:eu-south-1:709024702237:subgrp:cld-dbsubnet-devteam07
  DBSubnetGroupDescription: CLD-DBSUBNET-DEVTEAM07
  DBSubnetGroupName: cld-dbsubnet-devteam07
  SubnetGroupStatus: Complete
  Subnets:
  - SubnetAvailabilityZone:
      Name: eu-south-1b
    SubnetIdentifier: subnet-0e8ffe3df60187d0b
    SubnetOutpost: {}
    SubnetStatus: Active
  - SubnetAvailabilityZone:
      Name: eu-south-1a
    SubnetIdentifier: subnet-0e80be6dcbf923a59
    SubnetOutpost: {}
    SubnetStatus: Active
  SupportedNetworkTypes:
  - IPV4
  VpcId: vpc-0ae930118f4e3cf79
```

* Add subnets

| Variable          | Value                                                                                   |
| ----------------- | --------------------------------------------------------------------------------------- |
| Availabilily zone | eu-south-1a + eu-south-1b                                                               |
| Subnets           | <ul><li>10.0.7.0/28 -> eu-south-1a</li><li>10.0.07.128/28 -> eu-south-1b</li></ul> |

```
[INPUT]
CF : VOir la création du db-subnet-group, on a passé les subnets à ce moment.
[OUTPUT]
```

<figure><img src="../../../.gitbook/assets/image (1).png" alt=""><figcaption><p>Result expected</p></figcaption></figure>

### **Step 2: Create a new security group for the RDS instance**

{% hint style="info" %}
Go back to the VPC config panel !
{% endhint %}

| Rule                 | Value                                                                                                                                                       |
| -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Name                 | <mark style="color:red;">CLD-SG-PRIVATE-DEVOPSTEAM07-RDS</mark>                                                                                          |
| Description          | Same as name value                                                                                                                                          |
| Rule for SQL Traffic | <p>[INBOUND]</p><ul><li>Port range: 3306</li><li>Protocol: TCP</li><li>Source: Private Subnet e-south-1a + 1b</li></ul><p>[OUTBOUND]<br>No restrictions</p> |

```
[INPUT]
aws ec2 create-security-group \
    --group-name "CLD-SG-DEVOPSTEAM07_RDS" \
    --description "CLD-SG-DEVOPSTEAM07_RDS" \
    --vpc-id vpc-0ae930118f4e3cf79 \
    --tag-specifications 'ResourceType=security-group,Tags=[{Key=Name,Value=CLD-SG-DEVOPSTEAM07_RDS}]'
aws ec2 authorize-security-group-ingress \
    --group-id sg-0500e17929d894a7d \
    --ip-permissions FromPort=3306,ToPort=3306,IpProtocol=tcp,IpRanges='[{CidrIp=10.0.7.0/28,Description="3306 FROM eu-south-1a"}]'
[OUTPUT]
GroupId: sg-0500e17929d894a7d
Tags:
- Key: Name
  Value: CLD-SG-DEVOPSTEAM07_RDS
Return: true
SecurityGroupRules:
- CidrIpv4: 10.0.7.0/28
  Description: 3306 FROM eu-south-1a
  FromPort: 3306
  GroupId: sg-0500e17929d894a7d
  GroupOwnerId: '709024702237'
  IpProtocol: tcp
  IsEgress: false
  SecurityGroupRuleId: sgr-04101470b41329bb1
  ToPort: 3306
```

### **Step 3: Create a database**

{% hint style="info" %}
**This step will be performed by AWS Console.**\
****\
**Issue 1: no way  to specify the template with the CLI (prod/dev)**

**Issue 2: accessibility problem for the sql user created with the CLI.**
{% endhint %}

| Variable                  | Value                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Engine                    | Please refer to your Drupal version                                                                                                                                                                                                                                                                                                                                                                                                                     |
| Template                  | Dev / Test                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| DB instance identifier    | DBINS-DEVOPSTEAM\[XX]-RDS-DRUPAL                                                                                                                                                                                                                                                                                                                                                                                                                        |
| DB User                   | admin                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| DB Password               | e9B7PseS4VxvC$pZ                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| DB instance class         | <ul><li>Burstable classes</li><li>db.t3.micro</li></ul>                                                                                                                                                                                                                                                                                                                                                                                                 |
| Storage                   | <ul><li>Storage type: General Purpose (SSD)</li><li>Allocated Storage: 20 GB</li><li>Enable Storage Autoscalling: unchecked</li></ul>                                                                                                                                                                                                                                                                                                                   |
| Availability & durability | <ul><li>Do not create a standby instance</li></ul>                                                                                                                                                                                                                                                                                                                                                                                                      |
| Connectivity              | <ul><li>Don't connect to an EC2 compute resource</li><li>Ipv4 only</li><li>VPC: <mark style="color:red;">VPC-CLD</mark></li></ul><ul><li>Subnet Group: The one created just before (for your devopsteam)</li><li>Public access: No</li><li>VPC security group: The one created just before (for your devopsteam)</li><li>Availability zone : eu-south-1a</li><li>Certificate authority : default</li><li>Additional configuration : port 3306</li></ul> |

{% hint style="info" %}
Disable Monitoring option
{% endhint %}

{% hint style="info" %}
Disable all Additional configuration options

* Backup
* Encryption
* Log exports
* Maintenance
* Deletion protection
{% endhint %}

![aws rds creation](figures/capture-aws-rds.png)

### Step 4 : Deploy Drupal Instance

* Based on the previous AMI build, deploy a Drupal instance.

| Variable          | Value                               |
| ----------------- | ----------------------------------- |
| Name              | CLD-INSTANCE-DEVOPSTEAM\[XX]-DRUPAL |
| Type              | t3.micro                            |
| Availability Zone | eu-south-1a                         |
| Private Ip        | 10.0.\[XX].10                       |
| Security Group    | CLD-SG-PRIVATE-DEVOPSTEAM\[XX]      |
| Key Name          | CLD-KEY-DEVOPTEAM\[XX]-DRUPAL       |

```
[INPUT]
aws ec2 run-instances \
    --image-id "ami-05d3ed574e3c1b418" \
    --count 1 \
    --instance-type t3.micro \
    --key-name CLD-KEY-DEVOPSTEAM07-INSTANCE \
    --security-group-ids sg-0e81202b6aef37431 \
    --subnet-id subnet-0e80be6dcbf923a59 \
    --private-ip-address 10.0.7.10 \
    --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=CLD-INSTANCE-DEVOPSTEAM07-DRUPAL}]'
[OUTPUT]
Groups: []
Instances:
- AmiLaunchIndex: 0
  Architecture: x86_64
  BlockDeviceMappings: []
  CapacityReservationSpecification:
    CapacityReservationPreference: open
  ClientToken: d6c9a81c-b443-4436-9a64-9841714612bb
  CpuOptions:
    CoreCount: 1
    ThreadsPerCore: 2
  EbsOptimized: false
  EnaSupport: true
  EnclaveOptions:
    Enabled: false
  Hypervisor: xen
  ImageId: ami-05d3ed574e3c1b418
  InstanceId: i-08a161e13dd2797be
  InstanceType: t3.micro
  KeyName: CLD-KEY-DEVOPSTEAM07-INSTANCE
  LaunchTime: '2023-03-21T15:52:23+00:00'
  MaintenanceOptions:
    AutoRecovery: default
  MetadataOptions:
    HttpEndpoint: enabled
    HttpProtocolIpv6: disabled
    HttpPutResponseHopLimit: 1
    HttpTokens: optional
    InstanceMetadataTags: disabled
    State: pending
  Monitoring:
    State: disabled
  NetworkInterfaces:
  - Attachment:
      AttachTime: '2023-03-21T15:52:23+00:00'
      AttachmentId: eni-attach-0fa703ad06b739bbc
      DeleteOnTermination: true
      DeviceIndex: 0
      NetworkCardIndex: 0
      Status: attaching
    Description: ''
    Groups:
    - GroupId: sg-0e81202b6aef37431
      GroupName: CLD-SG-PRIVATE-DEVOPSTEAM07
    InterfaceType: interface
    Ipv6Addresses: []
    MacAddress: 06:eb:45:cd:cb:d6
    NetworkInterfaceId: eni-0be2bd9994481ee20
    OwnerId: '709024702237'
    PrivateIpAddress: 10.0.7.10
    PrivateIpAddresses:
    - Primary: true
      PrivateIpAddress: 10.0.7.10
    SourceDestCheck: true
    Status: in-use
    SubnetId: subnet-0e80be6dcbf923a59
    VpcId: vpc-0ae930118f4e3cf79
  Placement:
    AvailabilityZone: eu-south-1a
    GroupName: ''
    Tenancy: default
  PrivateDnsName: ip-10-0-7-10.eu-south-1.compute.internal
  PrivateDnsNameOptions:
    EnableResourceNameDnsAAAARecord: false
    EnableResourceNameDnsARecord: false
    HostnameType: ip-name
  PrivateIpAddress: 10.0.7.10
  ProductCodes: []
  PublicDnsName: ''
  RootDeviceName: /dev/xvda
  RootDeviceType: ebs
  SecurityGroups:
  - GroupId: sg-0e81202b6aef37431
    GroupName: CLD-SG-PRIVATE-DEVOPSTEAM07
  SourceDestCheck: true
  State:
    Code: 0
    Name: pending
  StateReason:
    Code: pending
    Message: pending
  StateTransitionReason: ''
  SubnetId: subnet-0e80be6dcbf923a59
  Tags:
  - Key: Name
    Value: CLD-INSTANCE-DEVOPSTEAM07-DRUPAL
  VirtualizationType: hvm
  VpcId: vpc-0ae930118f4e3cf79
OwnerId: '709024702237'
ReservationId: r-0d9614340a6146d01
(END)
  PrivateIpAddress: 10.0.7.10
  ProductCodes: []
  PublicDnsName: ''
  RootDeviceName: /dev/xvda
  RootDeviceType: ebs
  SecurityGroups:
  - GroupId: sg-0e81202b6aef37431
    GroupName: CLD-SG-PRIVATE-DEVOPSTEAM07
  SourceDestCheck: true
  State:
    Code: 0
    Name: pending
  StateReason:
    Code: pending
    Message: pending
  StateTransitionReason: ''
  SubnetId: subnet-0e80be6dcbf923a59
  Tags:
  - Key: Name
    Value: CLD-INSTANCE-DEVOPSTEAM07-DRUPAL
  VirtualizationType: hvm
  VpcId: vpc-0ae930118f4e3cf79
OwnerId: '709024702237'
ReservationId: r-0d9614340a6146d01
(END)
  PrivateIpAddress: 10.0.7.10
  ProductCodes: []
  PublicDnsName: ''
  RootDeviceName: /dev/xvda
  RootDeviceType: ebs
  SecurityGroups:
  - GroupId: sg-0e81202b6aef37431
    GroupName: CLD-SG-PRIVATE-DEVOPSTEAM07
  SourceDestCheck: true
  State:
    Code: 0
    Name: pending
  StateReason:
    Code: pending
    Message: pending
  StateTransitionReason: ''
  SubnetId: subnet-0e80be6dcbf923a59
  Tags:
  - Key: Name
    Value: CLD-INSTANCE-DEVOPSTEAM07-DRUPAL
  VirtualizationType: hvm
  VpcId: vpc-0ae930118f4e3cf79
OwnerId: '709024702237'
ReservationId: r-0d9614340a6146d01
```

### **Step 5 : Test connection**

* Get the RDS endpoint

<figure><img src="../../../.gitbook/assets/image.png" alt=""><figcaption></figcaption></figure>

```
[INPUT]
aws rds describe-db-instances --query 'DBInstances[*].[Endpoint.Address]' --filter Name=db-instance-id,Values=dbins-devopsteams07-rds-drupal
[OUTPUT]
- - dbins-devopsteams07-rds-drupal.cniwrjgeantd.eu-south-1.rds.amazonaws.com
```

* Test that the Drupal machine can connect to the RDS with the command:

```
mysql --host=endpoint_address --user=<rds_master_username> --password=<rds_master_password>`
If you have a prompt with `mysql>` it means that it worked
```

```
[INPUT]
mysql --host=dbins-devopsteams07-rds-drupal.cniwrjgeantd.eu-south-1.rds.amazonaws.com -u admin -p
[OUTPUT]
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 123
Server version: 10.6.12-MariaDB managed by https://aws.amazon.com/rds/

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]>
```

```
//Result expected
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 33
Server version: 10.6.12-MariaDB managed by https://aws.amazon.com/rds/

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
```

### **Step 6 : Migrate DB**

* (In prod, you should inform end user for maintenance)
* Clean active sessions
* Connect to the database and create the database and a new user

```sql
CREATE DATABASE drupal;
CREATE USER 'rds_admin'@'%' IDENTIFIED BY '<rds_admin_password>';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON drupal.* TO 'rds_admin'@'%';
```

```
[INPUT]
CREATE DATABASE drupal;
CREATE USER 'rds_admin'@'%' IDENTIFIED BY 'pahX2dVhrLRncD';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON drupal.* TO 'rds_admin'@'%';
[OUTPUT]
Query OK, 1 row affected (0.000 sec)
Query OK, 0 rows affected (0.001 sec)
Query OK, 0 rows affected (0.001 sec)
```

* Disconnect from RDS and use this command to migrate data from local database to the rds

```shell
mysqldump --add-drop-table --no-tablespaces --user=drupal --password=<mysql_local_password> drupal | mysql --host=endpoint_address --user=<rds_admin_password> --password=<rds_admin_password> drupal
```

```
[INPUT]
mysqldump --add-drop-table --no-tablespaces --user=drupal_user -p drupal_db | mysql --host=dbins-devopsteams07-rds-drupal.cniwrjgeantd.eu-south-1.rds.amazonaws.com --user=rds_admin --password=pahX2dVhrLRncD drupal
[OUTPUT]
None
```

* Edit the database settings in file `/var/www/html/drupal/sites/default/settings.php`

```
[INPUT]
$databases['default']['default'] = array (
  'database' => 'drupal',
  'username' => 'rds_admin',
  'password' => 'pahX2dVhrLRncD',
  'prefix' => '',
  'host' => 'dbins-devopsteams07-rds-drupal.cniwrjgeantd.eu-south-1.rds.amazonaws.com',
  'port' => '3306',
  'namespace' => 'Drupal\\mysql\\Driver\\Database\\mysql',
  'driver' => 'mysql',
  'autoload' => 'core/modules/mysql/src/Driver/Database/mysql/',
);
[OUTPUT]
None
```

### **Step 7 : Create a custom virtual machine image**

* In the EC2 console bring up the Instances panel and select the Drupal master instance.
* Bring up the context menu and select Image > Create Image. Provide the following answers (leave any field not mentioned at its default value):
* Image Name: DEVOPSTEAM\[XX]-RDS-DRUPAL-AMI.
* Image Description: Drupal connected to RDS database.
* Click Create Image. The instance will shut down temporarily and the image will be created.

```
[INPUT]
aws ec2 create-image \
    --instance-id i-08a161e13dd2797be \
    --name "DEVOPSTEAM07-RDS-DRUPAL-AMI" \
    --description "Drupal connected to RDS database"
[OUTPUT]
ImageId: ami-0df61dfbe6cc35d7e
```

### Conceptual aspects

#### Question 1 - Standard vs Memory-optimized vs Burstable?

![](../../../.gitbook/assets/DBInstanceClass.PNG)

Standards instances are general-purpose instances that are balanced between compute, network and memory resources.

Memory instances are designed to deliver fast performances for workloads that process a lot of datasets in memory. They're optimized for memory intensive applications.

Burstables instances are designed to provide a baseline level of CPU performances and to burst above the baseline when needed.

#### Question 2 - What's a Standby instance?

![](../../../.gitbook/assets/AvailabilityAndDurability.PNG)

A Standby instance in AWS refers to a replica of an Amazon RDS (Relational Database Service) instance that is kept on standby mode for high availability and disaster recovery purposes. It automatically syncs with the running instance. In case of a failure on main instance, AWS fails over to the Standby instance.

#### Question 3 - How to prove the correct operation of the RDS?

We added a new user with the Drupal interface and verified in the database if the newly added user was in there.  
