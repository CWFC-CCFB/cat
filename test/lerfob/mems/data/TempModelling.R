####################################################
# Getting soil temperature from air temperature
# @author Mathieu Fortin - July 2024
####################################################

library(reshape)

temp <- read.csv("SoilTemperatures.csv")

meltedData <- melt(temp, "Date")
meltedData$Year <- as.integer(substr(meltedData$Date,1,4))
meltedData$Month <- as.integer(substr(meltedData$Date,6,7))
meltedData$Day <- as.integer(substr(meltedData$Date,9,10))
colnames(meltedData)[2] <- "Site"
colnames(meltedData)[3] <- "SoilTempC"

soilTemperatures <- meltedData[,c("Site", "Year", "Month", "Day", "SoilTempC")]
rm(temp, meltedData)

library(BioSIM)

BioSIM::getModelList()

weather <- BioSIM::generateWeather(modelNames = "Climatic_Daily",
                        fromYr = 2012,
                        toYr = 2017,
                        id = "ForetMontmorency",
                        latDeg = 47.3148,
                        longDeg = -71.1409)$Climatic_Daily


soilAndAirTemp <- merge(soilTemperatures,
                        weather[,c("Year", "Month", "Day", "Tmin", "Tair", "Tmax")],
                        by = c("Year", "Month", "Day"))

soilAndAirTemp <- soilAndAirTemp[which(soilAndAirTemp$Site == "FOM.01.T"),]
soilAndAirTemp <- soilAndAirTemp[which(soilAndAirTemp$Year > 2012 & soilAndAirTemp$Year < 2017),]

soilAndAirTemp <- soilAndAirTemp[order(soilAndAirTemp$Site, soilAndAirTemp$Year, soilAndAirTemp$Month, soilAndAirTemp$Day),]

min(soilAndAirTemp$SoilTempC)
mean(soilAndAirTemp$SoilTempC)
max(soilAndAirTemp$SoilTempC)

plot(SoilTempC ~ Tair, soilAndAirTemp)

fit.nls <- nls(SoilTempC ~ b0 + b1 / (1 + exp(-(b2 + b3 * Tair))),
               data = soilAndAirTemp,
               start = list(b0 = -5, b1 = 25, b2 = -20, b3 = 2))
summary(fit.nls)
plot(fit.nls)
hist(resid(fit.nls))

