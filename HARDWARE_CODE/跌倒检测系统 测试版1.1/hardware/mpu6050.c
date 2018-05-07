//#include <stc12c5a60s2.h>
//#include <math.h>
#include "mpu6050.h"
#include "STMIIC.h"

/*
//////////////////////////////////////////
// ����MPU6050�ڲ���ַ
//////////////////////////////////////////
#define	SMPLRT_DIV		0x19	//�����ǲ����ʣ�����ֵ��0x07(125Hz)
#define	CONFIG			0x1A	//��ͨ�˲�Ƶ�ʣ�����ֵ��0x06(5Hz)
#define	GYRO_CONFIG		0x1B	//�������Լ켰������Χ������ֵ��0x18(���Լ죬2000deg/s)
#define	ACCEL_CONFIG	0x1C	//���ټ��Լ졢������Χ����ͨ�˲�Ƶ�ʣ�����ֵ��0x01(���Լ죬2G��5Hz)
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
#define	PWR_MGMT_1		0x6B	//��Դ��������ֵ��0x00(��������)
#define	WHO_AM_I		0x75	//IIC��ַ�Ĵ���(Ĭ����ֵ0x68��ֻ��)
#define	MPU6050Address	0xD0	//IICд��ʱ�ĵ�ַ�ֽ����ݣ�+1Ϊ��ȡ��6050�ĸ�7λ��ַΪ0x68���Ӷ�дλ���D0


void initmpu6050(void);
void ReadAll(void);
void GetData(void);

unsigned char mpudata[16] = {0xAA,0xAA,0x02,0x0C,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

int agd[6] = {0x00};//accelerate gyroscope data
*/

unsigned char mpudata[16] = {0xAA,0xAA,0x02,0x0C,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
short int agd[6] = {0x00};//accelerate gyroscope data���洢���ֽںϳɺ�����ݡ�

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
	
//	IICWriteOneByte(MPU6050Address, PWR_MGMT_1, 0x00);//��Դ��������ֵ��0x00(��������)
//	IICWriteOneByte(MPU6050Address, SMPLRT_DIV, 0x07);//�����ǲ����ʣ�����ֵ��0x07(125Hz)
//	IICWriteOneByte(CONFIG, 0x06);//��ͨ�˲�Ƶ�ʣ�����ֵ��0x06(5Hz)
//	IICWriteOneByte(GYRO_CONFIG, 0x18);//�������Լ켰������Χ������ֵ��0x18(���Լ죬2000deg/s)
//	IICWriteOneByte(ACCEL_CONFIG, 0x01);//���ټ��Լ졢������Χ����ͨ�˲�Ƶ�ʣ�����ֵ��0x01(���Լ죬2G��5Hz)
	
	//�����д������ñ��˵�����
//	IICWriteOneByte(MPU6050Address, CONFIG, 0x04);      //21HZ�˲� ��ʱA8.5ms G8.3ms  �˴�ȡֵӦ�൱ע�⣬��ʱ��ϵͳ�������Ϊ��
//	IICWriteOneByte(MPU6050Address, GYRO_CONFIG, 0x08); //������500��/S 65.5LSB/g
//	IICWriteOneByte(MPU6050Address, ACCEL_CONFIG, 0x08);//���ٶ�+-4g  8192LSB/g
	
	//�������ר�����ã�Ӧ�����Ǽ��Ϻ��ʵ�Ӳ���˲�
	IICWriteOneByte(MPU6050Address, PWR_MGMT_1, 0x00);//��Դ��������ֵ��0x00(��������)
	IICWriteOneByte(MPU6050Address, ACCEL_CONFIG, 0x10);//���ٶ�+-8g
	IICWriteOneByte(MPU6050Address, GYRO_CONFIG, 0x10); //������1000��/S 65.5LSB/g
}

//6050ֻ֧��2�ֽ�����
//��������е�����
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
