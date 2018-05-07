#include "usart2.h"
#include "math.h"

char LongtitudeDirection = 'E', LatitudeDirection = 'N';
double longtitude=0.0, latitude=0.0;

unsigned char getgps(char *p)
{
	unsigned short int i=0, j=1, length;//j用于记录已经找到的逗号的个数
	unsigned char e;//乘方运算的指数
	unsigned short int comma[13] = {0};//用于保存逗号的位置，为了方便处理，不用comma[0]
	int coordinate;//int差一点就保存不下坐标信息了，好险……18 0000 0000

	length = USART2_RX_STA & 0x7FFF;
	USART2_RX_STA = 0;
	
	//如果串口2的第1个字符不是$，说明收到的不是GPS信息，直接返回0
	//如果串口2的第4个字符不是R，说明收到的不是GPS定位RMC信息，
	//如果收到的数据长度小于20，说明收到的数据有误，返回0
	if(USART2_RX_BUF[0] != '$' || USART2_RX_BUF[3] != 'R' || (length) < 20)
	{
		return 0;
	}
	
	//找出所有逗号的位置
	for(i=0; i<(length); i++)
	{
		if(p[i] == 44)//44是逗号的ASCII码
		{
			comma[j] = i;
			j++;
		}
		//printf("%c", p[i]);
	}

	//如果没有位置信息，直接返回
	if(comma[4] - comma[3] == 1 || comma[6] - comma[5] == 1)//如果没有位置信息，直接返回
	{
		return 0;
	}

	//第3、4逗号之间保存的是纬度
	coordinate = 0;//先清零
	e = comma[4] - comma[3] - 3;//数值的总位数，为什么是减3，一位是逗号，一位是小数点，10的0次方才是1，所以还要再减一
	for(i=comma[3]+1; i<comma[4]; i++)//comma[3]+1才是数字开始的地方
	{
		//遇到小数点就跳过
		if(p[i] == 46)
		{
			i++;
		}
		
		//从高位开始把每一位都累加起来
		//ascii码中48~57是数字
		coordinate = (int)(coordinate + (p[i] - 48) * pow(10, e));
		e--;	
	}
	latitude = (double)coordinate / 100000;//固定格式，小数点后5位
	
	LatitudeDirection = p[comma[4]+1];//第四个逗号+1是纬度的方向。
//	printf("latitude:%f \n", latitude);


	//第5、6逗号之间保存的是经度
	coordinate = 0;//先清零
	e = comma[6] - comma[5] - 3;//数值的总位数，为什么是减3，一位是逗号，一位是小数点，10的0次方才是1，所以还要再减一
	for(i=comma[5]+1; i<comma[6]; i++)//comma[3]+1才是数字开始的地方
	{
		//遇到小数点就跳过
		if(p[i] == 46)
		{
			i++;
		}
		
		//从高位开始把每一位都累加起来
		//ascii码中48~57是数字
		coordinate = (int)(coordinate + (p[i] - 48) * pow(10, e));
		e--;	
	}
	longtitude = (double)coordinate / 100000;//固定格式，小数点后5位
	
	LongtitudeDirection = p[comma[6]+1];//第四个逗号+1是经度的方向。
//	printf("longtitude:%f \n", longtitude);

	return 1;//定位成功返回1
}

double ChangeCoord(double x)
{
	//计算公式
	//数据格式为abcde.fghi
	//计算公式为abc+(de/60)+(fghi/600000)
	//会有一定精度损失！！！
	//会有一定精度损失！！！
	//会有一定精度损失！！！
	//a = (float)((int)n/100 + (float)((((int)n % 100)/60)) + (float)((n - (int)n)/600000));//这样转换出的是int型数据，不能用！
	double a;

	a = (int)x/100;//获得abc
	a += (double)((int)x % 100)/60;//获得de/60
	a += (double)(x - (int)x) * 10000 / 600000;//获得fghi/600000

	return a;
}
