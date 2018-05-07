//#include <stc12c5a60s2.h>
//#include <math.h>
#include "mpu6050.h"
#include "STMIIC.h"

/*
//////////////////////////////////////////
// 定义MPU6050内部地址
//////////////////////////////////////////
#define	SMPLRT_DIV		0x19	//陀螺仪采样率，典型值：0x07(125Hz)
#define	CONFIG			0x1A	//低通滤波频率，典型值：0x06(5Hz)
#define	GYRO_CONFIG		0x1B	//陀螺仪自检及测量范围，典型值：0x18(不自检，2000deg/s)
#define	ACCEL_CONFIG	0x1C	//加速计自检、测量范围及高通滤波频率，典型值：0x01(不自检，2G，5Hz)
#define	ACCEL_XOUT_H	0x3B
#define	ACCEL_XOUT_L	0x3C
#define	ACCEL_YOUT_H	0x3D
#define	ACCEL_YOUT_L	0x3E
#define	ACCEL_ZOUT_H	0x3F
#define	ACCEL_ZOUT_L	0x40
#define	TEMP_OUT_H		0x41
#define	TEMP_OUT_L		0x42
#define	GYRO_XOUT_H		0x43
#define	GYRO_XOUT_L		0x44	
#define	GYRO_YOUT_H		0x45
#define	GYRO_YOUT_L		0x46
#define	GYRO_ZOUT_H		0x47
#define	GYRO_ZOUT_L		0x48
#define	PWR_MGMT_1		0x6B	//电源管理，典型值：0x00(正常启用)
#define	WHO_AM_I		0x75	//IIC地址寄存器(默认数值0x68，只读)
#define	MPU6050Address	0xD0	//IIC写入时的地址字节数据，+1为读取，6050的高7位地址为0x68，加读写位变成D0


void initmpu6050(void);
void ReadAll(void);
void GetData(void);

unsigned char mpudata[16] = {0xAA,0xAA,0x02,0x0C,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

int agd[6] = {0x00};//accelerate gyroscope data
*/

unsigned char mpudata[16] = {0xAA,0xAA,0x02,0x0C,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
short int agd[6] = {0x00};//accelerate gyroscope data，存储两字节合成后的数据。

void GetData()
{
	agd[0] = (mpudata[4] << 8) + mpudata[5];
	agd[1] = (mpudata[6] << 8) + mpudata[7];
	agd[2] = (mpudata[8] << 8) + mpudata[9];
	agd[3] = (mpudata[10] << 8) + mpudata[11];
	agd[4] = (mpudata[12] << 8) + mpudata[13];
	agd[5] = (mpudata[14] << 8) + mpudata[15];
}

void initmpu6050()
{
	//IICinit();
	
//	IICWriteOneByte(MPU6050Address, PWR_MGMT_1, 0x00);//电源管理，典型值：0x00(正常启用)
//	IICWriteOneByte(MPU6050Address, SMPLRT_DIV, 0x07);//陀螺仪采样率，典型值：0x07(125Hz)
//	IICWriteOneByte(CONFIG, 0x06);//低通滤波频率，典型值：0x06(5Hz)
//	IICWriteOneByte(GYRO_CONFIG, 0x18);//陀螺仪自检及测量范围，典型值：0x18(不自检，2000deg/s)
//	IICWriteOneByte(ACCEL_CONFIG, 0x01);//加速计自检、测量范围及高通滤波频率，典型值：0x01(不自检，2G，5Hz)
	
	//这三行代码引用别人的设置
//	IICWriteOneByte(MPU6050Address, CONFIG, 0x04);      //21HZ滤波 延时A8.5ms G8.3ms  此处取值应相当注意，延时与系统周期相近为宜
//	IICWriteOneByte(MPU6050Address, GYRO_CONFIG, 0x08); //陀螺仪500度/S 65.5LSB/g
//	IICWriteOneByte(MPU6050Address, ACCEL_CONFIG, 0x08);//加速度+-4g  8192LSB/g
	
	//跌倒检测专用设置，应当考虑加上合适的硬件滤波
	IICWriteOneByte(MPU6050Address, PWR_MGMT_1, 0x00);//电源管理，典型值：0x00(正常启用)
	IICWriteOneByte(MPU6050Address, ACCEL_CONFIG, 0x10);//加速度+-8g
	IICWriteOneByte(MPU6050Address, GYRO_CONFIG, 0x10); //陀螺仪1000度/S 65.5LSB/g
}

//6050只支持2字节连读
//这个函数有点罗嗦
void ReadAll()
{
	IICstart();
	IICwrite(MPU6050Address);
	SlaRes();
	IICwrite(ACCEL_XOUT_H);
	SlaRes();
	IICstart();
	IICwrite(MPU6050Address+1);
	SlaRes();
	mpudata[4] = IICread();
	MasRes();
	mpudata[5] = IICread();
	NoRes();
	IICstop();

	IICstart();
	IICwrite(MPU6050Address);
	SlaRes();
	IICwrite(ACCEL_YOUT_H);
	SlaRes();
	IICstart();
	IICwrite(MPU6050Address+1);
	SlaRes();
	mpudata[6] = IICread();
	MasRes();
	mpudata[7] = IICread();
	NoRes();
	IICstop();

	IICstart();
	IICwrite(MPU6050Address);
	SlaRes();
	IICwrite(ACCEL_ZOUT_H);
	SlaRes();
	IICstart();
	IICwrite(MPU6050Address+1);
	SlaRes();
	mpudata[8] = IICread();
	MasRes();
	mpudata[9] = IICread();
	NoRes();
	IICstop();

	IICstart();
	IICwrite(MPU6050Address);
	SlaRes();
	IICwrite(GYRO_XOUT_H);
	SlaRes();
	IICstart();
	IICwrite(MPU6050Address+1);
	SlaRes();
	mpudata[10] = IICread();
	MasRes();
	mpudata[11] = IICread();
	NoRes();
	IICstop();
	
	IICstart();
	IICwrite(MPU6050Address);
	SlaRes();
	IICwrite(GYRO_YOUT_H);
	SlaRes();
	IICstart();
	IICwrite(MPU6050Address+1);
	SlaRes();
	mpudata[12] = IICread();
	MasRes();
	mpudata[13] = IICread();
	NoRes();
	IICstop();

	IICstart();
	IICwrite(MPU6050Address);
	SlaRes();
	IICwrite(GYRO_ZOUT_H);
	SlaRes();
	IICstart();
	IICwrite(MPU6050Address+1);
	SlaRes();
	mpudata[14] = IICread();
	MasRes();
	mpudata[15] = IICread();
	NoRes();
	IICstop();
}
