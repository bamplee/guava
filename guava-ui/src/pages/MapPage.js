import React from 'react';
import GuavaMainHeader from '../components/header/GuavaMainHeader';
import GuavaMap from '../components/map/GuavaMap';
import {TabBar} from "antd-mobile";
import {EnvironmentOutlined} from "@ant-design/icons";

const MapPage = () => {
    return (
        <>
            <GuavaMainHeader/>
            <GuavaMap/>
        </>
    );
};

export default MapPage;